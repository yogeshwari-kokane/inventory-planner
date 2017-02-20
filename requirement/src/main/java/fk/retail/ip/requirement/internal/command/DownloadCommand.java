package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.FsnBand;
import fk.retail.ip.requirement.internal.entities.LastAppSupplier;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.WeeklySale;

import com.google.common.collect.Lists;


import fk.retail.ip.requirement.internal.entities.*;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.LastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import fk.retail.ip.requirement.internal.enums.RequirementState;

import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.ws.rs.core.StreamingOutput;

import fk.retail.ip.zulu.client.ZuluClient;
import fk.retail.ip.zulu.internal.entities.RetailProductAttributeResponse;

import org.apache.commons.collections4.map.MultiKeyMap;

import static java.util.stream.Collectors.toList;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public abstract class DownloadCommand {

    protected Set<String> requirementFsns;
    private final FsnBandRepository fsnBandRepository;
    private final WeeklySaleRepository weeklySaleRepository;
    private final ProductInfoRepository productInfoRepository;
    protected List<RequirementDownloadLineItem> requirementDownloadLineItems;
    private Map<String, List<RequirementDownloadLineItem>> fsnToRequirement;
    private final LastAppSupplierRepository lastAppSupplierRepository;
    private final GenerateExcelCommand generateExcelCommand;
    private final RequirementRepository requirementRepository;

    private final ZuluClient zuluClient;

    public DownloadCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand, LastAppSupplierRepository lastAppSupplierRepository,
                           ProductInfoRepository productInfoRepository, ZuluClient zuluClient, RequirementRepository requirementRepository) {

        this.fsnBandRepository = fsnBandRepository;
        this.weeklySaleRepository = weeklySaleRepository;
        this.generateExcelCommand = generateExcelCommand;
        this.productInfoRepository = productInfoRepository;
        this.zuluClient = zuluClient;
        this.lastAppSupplierRepository = lastAppSupplierRepository;
        this.requirementRepository = requirementRepository;

    }

    public StreamingOutput execute(List<Requirement> requirements, boolean isLastAppSupplierRequired) {

        requirementDownloadLineItems = requirements.stream().map(RequirementDownloadLineItem::new).collect(toList());
        fsnToRequirement = requirementDownloadLineItems.stream().collect(Collectors.groupingBy(RequirementDownloadLineItem::getFsn));
        requirementFsns = fsnToRequirement.keySet();
        fetchProductData();
        fetchFsnBandData();
        fetchSalesBucketData();
        fetchRequirementStateData(isLastAppSupplierRequired);

        return generateExcelCommand.generateExcel(requirementDownloadLineItems, getTemplateName(isLastAppSupplierRequired));
    }

    /*
    Fetch product data for list of fsns from db. If not found from db fetch the data from zulu.
    *
    * */
    protected void fetchProductData() {
        List<String> cachedFsns = fetchDataFromProductInfo();
        fetchDataFromZulu(cachedFsns);
    }

    protected void fetchFsnBandData() {
        List<FsnBand> bands = fsnBandRepository.fetchBandDataForFSNs(requirementFsns);
        bands.stream().forEach(b -> {
            List<RequirementDownloadLineItem> items = fsnToRequirement.get(b.getFsn());
            items.forEach(i -> {
                i.setPvBand(b.getPvBand());
                i.setSalesBand(b.getSalesBand());
            });
        });
    }

    protected void fetchSalesBucketData() {
        List<WeeklySale> sales = weeklySaleRepository.fetchWeeklySalesForFsns(requirementFsns);
        MultiKeyMap<String, Integer> fsnWhWeekSalesMap = new MultiKeyMap();
        sales.forEach(s -> fsnWhWeekSalesMap.put(s.getFsn(), s.getWarehouse(), String.valueOf(s.getWeek()), s.getSaleQty()));
        LocalDate date = LocalDate.now();
        TemporalField weekOfYear = WeekFields.of(DayOfWeek.MONDAY, 1).weekOfWeekBasedYear();
        int currentWeek = date.get(weekOfYear);
        requirementDownloadLineItems.forEach(reqItem
                -> populateSalesData(fsnWhWeekSalesMap, currentWeek, reqItem, reqItem::setWeek0Sale, reqItem::setWeek1Sale, reqItem::setWeek2Sale, reqItem::setWeek3Sale, reqItem::setWeek4Sale, reqItem::setWeek5Sale, reqItem::setWeek6Sale, reqItem::setWeek7Sale)
        );
    }

    private void populateSalesData(MultiKeyMap<String,Integer> fsnWhWeekSalesMap, int currentWeek, RequirementDownloadLineItem reqItem, Consumer<Integer>... setters) {
        for (Consumer<Integer> setter : setters) {
            Integer saleQty = fsnWhWeekSalesMap.get(reqItem.getFsn(), reqItem.getWarehouse(), String.valueOf(currentWeek));
            setter.accept(saleQty == null ? 0 : saleQty);
            currentWeek = (currentWeek - 2 + 52) % 52 + 1;
        }
    }

    protected void fetchLastAppSupplierDataFromProc() {
        List<LastAppSupplier> lastAppSuppliers = lastAppSupplierRepository.fetchLastAppSupplierForFsns(requirementFsns);
        MultiKeyMap<String,Integer> fsnWhLastAppMap = new MultiKeyMap();
        MultiKeyMap<String,String> fsnWhLastSupplierMap = new MultiKeyMap();
        lastAppSuppliers.forEach(l -> {
            fsnWhLastAppMap.put(l.getFsn(),l.getWarehouse(),l.getLastApp());
            fsnWhLastSupplierMap.put(l.getFsn(),l.getWarehouse(),l.getLastSupplier());
        });

        requirementDownloadLineItems.forEach(reqItem
                        -> {
                    if (fsnWhLastAppMap.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                        reqItem.setLastApp(fsnWhLastAppMap.get(reqItem.getFsn(),reqItem.getWarehouse()));
                    if (fsnWhLastSupplierMap.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                        reqItem.setLastSupplier(fsnWhLastSupplierMap.get(reqItem.getFsn(),reqItem.getWarehouse()));
                }
        );
    }


    protected void populateBizFinData() {
        List<Requirement> requirements = requirementRepository.findEnabledRequirementsByStateFsn("bizfin_review",requirementFsns);
        MultiKeyMap<String,Integer> fsnWhBizFinRecommended = new MultiKeyMap();
        MultiKeyMap<String,String> fsnWhBizFinComment = new MultiKeyMap();
        requirements.forEach(r -> {
            fsnWhBizFinRecommended.put(r.getFsn(),r.getWarehouse(),r.getQuantity());
            fsnWhBizFinComment.put(r.getFsn(),r.getWarehouse(),r.getOverrideComment());
        });

        requirementDownloadLineItems.forEach(reqItem
                -> {
            if (fsnWhBizFinRecommended.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                reqItem.setBizFinRecommendedQuantity(fsnWhBizFinRecommended.get(reqItem.getFsn(), reqItem.getWarehouse()));
            if (fsnWhBizFinComment.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                reqItem.setBizFinComment(fsnWhBizFinComment.get(reqItem.getFsn(), reqItem.getWarehouse()));
        });
    }


    protected void populateIpcQuantity() {
        List<Requirement> requirements = requirementRepository.findEnabledRequirementsByStateFsn("proposed",requirementFsns);
        MultiKeyMap<String,Integer> fsnWhIpcProposedQuantity = new MultiKeyMap();
        requirements.forEach(r -> {
            fsnWhIpcProposedQuantity.put(r.getFsn(),r.getWarehouse(),r.getQuantity());
});

        requirementDownloadLineItems.forEach(reqItem
                -> {
            if (fsnWhIpcProposedQuantity.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                reqItem.setIpcProposedQuantity(fsnWhIpcProposedQuantity.get(reqItem.getFsn(),reqItem.getWarehouse()));
        });
    }

    private void fetchDataFromZulu(List<String> cachedFsns) {
        RetailProductAttributeResponse retailProductAttributeResponse = zuluClient.getRetailProductAttributes(cachedFsns);
        retailProductAttributeResponse.getEntityViews().forEach(entityView -> {
            String fsn = entityView.getEntityId();
            List<RequirementDownloadLineItem> items = fsnToRequirement.get(fsn);
            Map<String, String> analyticalInfo = (Map<String, String>)entityView.getView().get("analytics_info");
            HashMap<Object, Object> supplyChain = (HashMap<Object, Object>)entityView.getView().get("supply_chain");
            String vertical = analyticalInfo.get("vertical");
            String category = analyticalInfo.get("category");
            String superCategory = analyticalInfo.get("super_category");
            Map<String, String> productAttributes = (Map<String, String>)(supplyChain.get("product_attributes"));
            String brand = productAttributes.get("brand");
            int fsp = Integer.parseInt(productAttributes.get("flipkart_selling_price"));
            String title = supplyChain.get("procurement_title").toString();
                items.forEach(i -> {
                    i.setVertical(vertical);
                    i.setCategory(category);
                    i.setSuperCategory(superCategory);
                    i.setBrand(brand);
                    i.setFsp(fsp);
                    i.setTitle(title);
                });
        });
    }

    private List<String> fetchDataFromProductInfo() {
        List<ProductInfo> productInfo = productInfoRepository.getProductInfo(Lists.newArrayList(requirementFsns));
        List<String> cachedFsns = Lists.newArrayList();
        productInfo.stream().forEach(pi -> {
        cachedFsns.add(pi.getFsn());
        List<RequirementDownloadLineItem> items = fsnToRequirement.get(pi.getFsn());
        items.forEach(i -> {
            i.setVertical(pi.getVertical());
            i.setCategory(pi.getCategory());
            i.setSuperCategory(pi.getSuperCategory());
            i.setTitle(pi.getTitle());
            i.setFsp(pi.getFsp());
            i.setBrand(pi.getBrand());
            });
        });
        List<String> leftOverFsns = Lists.newArrayList();
        requirementFsns.removeAll(cachedFsns);
        leftOverFsns.addAll(requirementFsns);
        return leftOverFsns;
      
      
    protected void populateCdoData() {
        List<Requirement> requirements = requirementRepository.findEnabledRequirementsByStateFsn("cdo_review",requirementFsns);
        MultiKeyMap<String,String> fsnWhCdoComment = new MultiKeyMap();
        MultiKeyMap<String,Integer> fsnWhQuantity = new MultiKeyMap();
        requirements.forEach(r -> {
            fsnWhCdoComment.put(r.getFsn(),r.getWarehouse(),r.getOverrideComment());
            fsnWhQuantity.put(r.getFsn(),r.getWarehouse(),r.getQuantity());
        });

        requirementDownloadLineItems.forEach(reqItem
                -> {
            if (fsnWhCdoComment.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                reqItem.setCdoOverrideReason(fsnWhCdoComment.get(reqItem.getFsn(),reqItem.getWarehouse()));
            if (fsnWhQuantity.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                reqItem.setQuantity(fsnWhQuantity.get(reqItem.getFsn(),reqItem.getWarehouse()));
        });
    }

    protected abstract String getTemplateName(boolean isLastAppSupplierRequired);

    abstract void fetchRequirementStateData(boolean isLastAppSupplierRequired);

}
