package fk.retail.ip.requirement.internal.command;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fk.retail.ip.requirement.internal.entities.FsnBand;
import fk.retail.ip.requirement.internal.entities.LastAppSupplier;
import fk.retail.ip.requirement.internal.entities.ProductInfo;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.Warehouse;
import fk.retail.ip.requirement.internal.entities.WeeklySale;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalStates;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.LastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.WarehouseRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.zulu.client.ZuluClient;
import fk.retail.ip.zulu.internal.entities.RetailProductAttributeResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.json.JSONObject;

import static java.util.stream.Collectors.toList;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public abstract class DownloadCommand {

    private final FsnBandRepository fsnBandRepository;
    private final WeeklySaleRepository weeklySaleRepository;
    private final ProductInfoRepository productInfoRepository;
    private final LastAppSupplierRepository lastAppSupplierRepository;
    private final GenerateExcelCommand generateExcelCommand;
    private final RequirementRepository requirementRepository;
    private final WarehouseRepository warehouseRepository;

    private final ZuluClient zuluClient;

    public DownloadCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand, LastAppSupplierRepository lastAppSupplierRepository,
                           ProductInfoRepository productInfoRepository, ZuluClient zuluClient, RequirementRepository requirementRepository, WarehouseRepository warehouseRepository) {

        this.fsnBandRepository = fsnBandRepository;
        this.weeklySaleRepository = weeklySaleRepository;
        this.generateExcelCommand = generateExcelCommand;
        this.productInfoRepository = productInfoRepository;
        this.zuluClient = zuluClient;
        this.lastAppSupplierRepository = lastAppSupplierRepository;
        this.requirementRepository = requirementRepository;
        this.warehouseRepository = warehouseRepository;

    }

    public StreamingOutput execute(List<Requirement> requirements, boolean isLastAppSupplierRequired) {

        List<RequirementDownloadLineItem> requirementDownloadLineItems = requirements.stream().map(RequirementDownloadLineItem::new).collect(toList());
        Map<String, List<RequirementDownloadLineItem>> fsnToRequirement = requirementDownloadLineItems.stream().collect(Collectors.groupingBy(RequirementDownloadLineItem::getFsn));
        Set<String> requirementFsns = fsnToRequirement.keySet();
        Map<String, List<RequirementDownloadLineItem>> WhToRequirement = requirementDownloadLineItems.stream().collect(Collectors.groupingBy(RequirementDownloadLineItem::getWarehouse));
        Set<String> requirementWhs =  WhToRequirement.keySet();
        fetchProductData(requirementFsns,fsnToRequirement );
        fetchFsnBandData(requirementFsns,fsnToRequirement);
        fetchSalesBucketData(requirementFsns,requirementDownloadLineItems);
        fetchWarehouseName(requirementWhs,requirementDownloadLineItems);
        fetchRequirementStateData(isLastAppSupplierRequired, requirementFsns,requirementDownloadLineItems);

        return generateExcelCommand.generateExcel(requirementDownloadLineItems, getTemplateName(isLastAppSupplierRequired));
    }

    /*
    Fetch product data for list of fsns from db. If not found from db fetch the data from zulu.
    *
    * */
    protected void fetchProductData(Set<String> requirementFsns, Map<String, List<RequirementDownloadLineItem>> fsnToRequirement) {
        Set<String> zuluFsns = fetchDataFromProductInfo(requirementFsns, fsnToRequirement);
        if (zuluFsns.size() > 0) {
            fetchDataFromZulu(zuluFsns, fsnToRequirement);
        }
    }

    protected void fetchFsnBandData(Set<String> requirementFsns, Map<String, List<RequirementDownloadLineItem>> fsnToRequirement) {
        List<FsnBand> bands = fsnBandRepository.fetchBandDataForFSNs(requirementFsns);
        bands.stream().forEach(b -> {
            List<RequirementDownloadLineItem> items = fsnToRequirement.get(b.getFsn());
            items.forEach(i -> {
                i.setPvBand(b.getPvBand());
                i.setSalesBand(b.getSalesBand());
            });
        });
    }

    protected void fetchSalesBucketData(Set<String> requirementFsns, List<RequirementDownloadLineItem> requirementDownloadLineItems) {
        List<WeeklySale> sales = weeklySaleRepository.fetchWeeklySalesForFsns(requirementFsns);
        MultiKeyMap<String, Integer> fsnWhWeekSalesMap = new MultiKeyMap();
        sales.forEach(s -> fsnWhWeekSalesMap.put(s.getFsn(), s.getWarehouse(), String.valueOf(s.getWeek()), s.getSaleQty()));
        LocalDate date = LocalDate.now();
        TemporalField weekOfYear = WeekFields.of(DayOfWeek.MONDAY, 2).weekOfWeekBasedYear();
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

    protected void fetchWarehouseName(Set<String> requirementWhs, List<RequirementDownloadLineItem> requirementDownloadLineItems) {
        List<Warehouse> warehouses = warehouseRepository.fetchWarehouseNameByCode(requirementWhs);
        HashMap<String,String> whCodeNameMap= new HashMap();
        warehouses.forEach(w -> {
         whCodeNameMap.put(w.getWarehouseCode(),w.getWarehouseName());
        });

        requirementDownloadLineItems.forEach(reqItem -> {
            if (whCodeNameMap.get(reqItem.getWarehouse())!=null)
                reqItem.setWarehouseName(whCodeNameMap.get(reqItem.getWarehouse()));
            else
                reqItem.setWarehouseName(reqItem.getWarehouse());
        });
    }


    protected void fetchLastAppSupplierDataFromProc(Set<String> requirementFsns, List<RequirementDownloadLineItem> requirementDownloadLineItems) {
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


    protected void populateBizFinData(Set<String> requirementFsns, List<RequirementDownloadLineItem> requirementDownloadLineItems) {

        List<Requirement> requirements = requirementRepository.findEnabledRequirementsByStateFsn(RequirementApprovalStates.BIZFIN_REVIEW.toString(),requirementFsns);
        MultiKeyMap<String,Integer> fsnWhBizFinRecommended = new MultiKeyMap();
        MultiKeyMap<String,String> fsnWhBizFinComment = new MultiKeyMap();
        requirements.forEach(r -> {
            fsnWhBizFinRecommended.put(r.getFsn(),r.getWarehouse(),r.getQuantity());
            fsnWhBizFinComment.put(r.getFsn(),r.getWarehouse(),r.getOverrideComment());
        });

        requirementDownloadLineItems.forEach(reqItem
                -> {
            if (fsnWhBizFinRecommended.get(reqItem.getFsn(),reqItem.getWarehouse())!=null && fsnWhBizFinRecommended.get(reqItem.getFsn(),reqItem.getWarehouse()) != reqItem.getQuantity())
                reqItem.setBizFinRecommendedQuantity(fsnWhBizFinRecommended.get(reqItem.getFsn(), reqItem.getWarehouse()));
            if (fsnWhBizFinComment.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                reqItem.setBizFinComment(fsnWhBizFinComment.get(reqItem.getFsn(), reqItem.getWarehouse()));
        });
    }


    protected void populateIpcQuantity(Set<String> requirementFsns, List<RequirementDownloadLineItem> requirementDownloadLineItems) {

        List<Requirement> requirements = requirementRepository.findEnabledRequirementsByStateFsn(RequirementApprovalStates.PROPOSED.toString(),requirementFsns);
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

    private void fetchDataFromZulu(Set<String> zuluFsns, Map<String, List<RequirementDownloadLineItem>> fsnToRequirement ) {
        List<String> zuluFsnList = Lists.newArrayList(zuluFsns);
        RetailProductAttributeResponse retailProductAttributeResponse = zuluClient.getRetailProductAttributes(zuluFsnList);
        retailProductAttributeResponse.getEntityViews().forEach(entityView -> {
            String fsn = entityView.getEntityId();
            List<RequirementDownloadLineItem> items = fsnToRequirement.get(fsn);
            Map<String, String> analyticalInfo = (Map<String, String>)entityView.getView().get("analytics_info");
            JSONObject supplyChainJson = new JSONObject(entityView.getView().get("supply_chain").toString());
            String vertical = analyticalInfo.get("vertical");
            String category = analyticalInfo.get("category");
            String superCategory = analyticalInfo.get("super_category");
            JSONObject productAttributesJson = new JSONObject(supplyChainJson.get("product_attributes").toString());
            String brand = productAttributesJson.get("brand").toString();
            int fsp;
            if (productAttributesJson.has("flipkart_selling_price")) {
                fsp = Integer.parseInt(productAttributesJson.get("flipkart_selling_price").toString());
            } else {
                fsp = -1;
            }

            String title = supplyChainJson.get("procurement_title").toString();
            items.forEach(i -> {
                i.setVertical(vertical);
                i.setCategory(category);
                i.setSuperCategory(superCategory);
                i.setBrand(brand);
                i.setFsp(fsp);
                i.setTitle(title);
            });
            ProductInfo productInfo = new ProductInfo();
            productInfo.setFsn(fsn);
            productInfo.setBrand(brand);
            productInfo.setCategory(category);
            productInfo.setFsp(fsp);
            productInfo.setSuperCategory(superCategory);
            productInfo.setTitle(title);
            productInfo.setVertical(vertical);
            productInfoRepository.persist(productInfo);

        });
    }

    private Set<String> fetchDataFromProductInfo(Set<String> requirementFsns, Map<String, List<RequirementDownloadLineItem>> fsnToRequirement ) {
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
        Set<String> cachedFsnSet = new HashSet<String>(cachedFsns);
        Set<String> requirementFsnsCopy = Sets.newHashSet(requirementFsns);
        requirementFsnsCopy.removeAll(cachedFsnSet);
        return requirementFsnsCopy;
    }



    protected void populateCdoData(Set<String> requirementFsns, List<RequirementDownloadLineItem> requirementDownloadLineItems) {
        List<Requirement> requirements = requirementRepository.findEnabledRequirementsByStateFsn(RequirementApprovalStates.CDO_REVIEW.toString(),requirementFsns);
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

    abstract void fetchRequirementStateData(boolean isLastAppSupplierRequired, Set<String> requirementFsns, List<RequirementDownloadLineItem> requirementDownloadLineItems);

}
