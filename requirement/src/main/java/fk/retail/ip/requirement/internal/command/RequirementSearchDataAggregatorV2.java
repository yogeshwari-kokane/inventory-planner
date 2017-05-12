package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fk.retail.ip.requirement.internal.entities.*;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.*;
import fk.retail.ip.requirement.model.RequirementSearchLineItem;
import fk.retail.ip.requirement.model.RequirementSearchV2LineItem;
import fk.retail.ip.requirement.model.SearchResponseV2;
import fk.retail.ip.zulu.client.ZuluClient;
import fk.retail.ip.zulu.internal.entities.RetailProductAttributeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by yogeshwari.k on 11/05/17.
 */
@Slf4j
public class RequirementSearchDataAggregatorV2 {

    private final FsnBandRepository fsnBandRepository;
    private final WeeklySaleRepository weeklySaleRepository;
    private final ProductInfoRepository productInfoRepository;
    private final RequirementRepository requirementRepository;
    private final WarehouseRepository warehouseRepository;
    private final ZuluClient zuluClient;
    private final GroupFsnRepository groupFsnRepository;
    private final GroupRepository groupRepository;

    public RequirementSearchDataAggregatorV2(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository,
                                           ProductInfoRepository productInfoRepository, ZuluClient zuluClient,
                                           RequirementRepository requirementRepository, WarehouseRepository warehouseRepository,
                                           GroupFsnRepository groupFsnRepository, GroupRepository groupRepository) {

        this.fsnBandRepository = fsnBandRepository;
        this.weeklySaleRepository = weeklySaleRepository;
        this.productInfoRepository = productInfoRepository;
        this.zuluClient = zuluClient;
        this.requirementRepository = requirementRepository;
        this.warehouseRepository = warehouseRepository;
        this.groupFsnRepository = groupFsnRepository;
        this.groupRepository = groupRepository;

    }

    /*
 Fetch product data for list of fsns from db. If not found from db fetch the data from zulu.
 *
 * */

    protected void fetchProductData(Map<String, SearchResponseV2> fsnToSearchResponse) {
        log.info("Fetching Product Data for search requirements");
        Set<String> zuluFsns = fetchDataFromProductInfo(fsnToSearchResponse);
        if (zuluFsns.size() > 0) {
            log.info("Fetching product data from Zulu for search for fns size " + zuluFsns.size());
            fetchDataFromZulu(zuluFsns, fsnToSearchResponse);
        }
    }

    protected void fetchGroupData(Map<String, SearchResponseV2> fsnToSearchResponse, String groupName) {
        log.info("Fetching group data from db for search requirements");
        if(groupName!=null && !isEmptyString(groupName)) {
            Set<String> groupNames = new HashSet<>();
            groupNames.add(groupName);
            List<Group> groupList = groupRepository.findByGroupNames(groupNames);
            Long groupId = groupList.get(0).getId();
            for (Map.Entry<String, SearchResponseV2> entry : fsnToSearchResponse.entrySet()) {
                SearchResponseV2 searchResponse = entry.getValue();
                searchResponse.setGroupName(groupName);
                searchResponse.setGroupId(groupId);
            }
            return;
        }

        Set<String> fsns = fsnToSearchResponse.keySet();
        List<GroupFsn> groupFsnList = groupFsnRepository.findByFsns(fsns);
        groupFsnList.stream().forEach(gf -> {
            SearchResponseV2 searchResponse = fsnToSearchResponse.get(gf.getFsn());
            searchResponse.setGroupId(gf.getGroup().getId());
            searchResponse.setGroupName(gf.getGroup().getName());
        });
    }

    protected void fetchFsnBandData(Map<String, SearchResponseV2> fsnToSearchResponse) {
        log.info("Fetching Fsn Band data for search requirements");
        Set<String> fsns = fsnToSearchResponse.keySet();
        List<FsnBand> bands = fsnBandRepository.fetchBandDataForFSNs(fsns);
        bands.stream().forEach(b -> {
            SearchResponseV2 searchResponse = new SearchResponseV2();
            searchResponse.setPvBand(b.getPvBand());
            searchResponse.setSalesBand(b.getSalesBand());
        });
    }

    protected void fetchSalesBucketData(Set<String> fsns, List<RequirementSearchV2LineItem> requirementSearchLineItems) {
        log.info("Fetching sales Bucket Data for search requirements");
        log.info("Start: get sales bucket data for fsns");
        List<WeeklySale> sales = weeklySaleRepository.fetchWeeklySalesForFsns(fsns);
        log.info("Finish: get sales bucket data for fsns");
        MultiKeyMap<String, Integer> fsnWhWeekSalesMap = new MultiKeyMap();
        sales.forEach(s -> fsnWhWeekSalesMap.put(s.getFsn(), s.getWarehouse(), String.valueOf(s.getWeek()), s.getSaleQty()));
        LocalDate date = LocalDate.now();
        TemporalField weekOfYear = WeekFields.of(DayOfWeek.MONDAY, 2).weekOfWeekBasedYear();
        int currentWeek = date.get(weekOfYear);
        requirementSearchLineItems.forEach(reqItem
                -> populateSalesData(fsnWhWeekSalesMap, currentWeek, reqItem)
        );
    }

    protected void fetchWarehouseName(List<RequirementSearchV2LineItem> requirementSearchLineItems) {
        Map<String, List<RequirementSearchV2LineItem>> warehouseToRequirement = requirementSearchLineItems.stream()
                .collect(Collectors.groupingBy(RequirementSearchV2LineItem::getWarehouse));
        Set<String> whCodes = warehouseToRequirement.keySet();
        log.info("Start: get warehouse data for fsns");
        List<Warehouse> warehouses = warehouseRepository.fetchWarehouseNameByCode(whCodes);
        log.info("Finish: get warehouse data for fsns");
        Map<String, String> warehouseCodeToName = warehouses.stream()
                .collect(Collectors.toMap(Warehouse::getCode, Warehouse::getName));
        requirementSearchLineItems.forEach(reqItem -> {
            String whName = warehouseCodeToName.get(reqItem.getWarehouse());
            reqItem.setWarehouseName(whName);
        });
    }

    protected MultiKeyMap<String, Integer> fetchCdoQuantity(List<Requirement> requirements) {
        Map<String, List<Requirement>> fsnToRequirement = requirements.stream().collect(Collectors.groupingBy(Requirement::getFsn));
        Set<String> fsns = fsnToRequirement.keySet();
        List<Requirement> cdoRequirements = requirementRepository.findEnabledRequirementsByStateFsn(RequirementApprovalState.CDO_REVIEW.toString(),fsns);
        MultiKeyMap<String, Integer> fsnWhQuantity = new MultiKeyMap();
        cdoRequirements.forEach(r -> {
            fsnWhQuantity.put(r.getFsn(), r.getWarehouse(), (int) r.getQuantity());
        });
        return fsnWhQuantity;
    }

    private void populateSalesData(MultiKeyMap<String,Integer> fsnWhWeekSalesMap, int currentWeek, RequirementSearchV2LineItem reqItem) {
        List<Integer> weeklySales = Lists.newArrayList();
        for (int i = 0; i < 8; i++) {
            Integer saleQty = fsnWhWeekSalesMap.get(reqItem.getFsn(), reqItem.getWarehouse(), String.valueOf(currentWeek));
            weeklySales.add(saleQty == null ? 0 : saleQty);
            currentWeek = (currentWeek - 2 + 52) % 52 + 1;
        }
        reqItem.setWeeklySales(weeklySales);
    }

    private void fetchDataFromZulu(Set<String> zuluFsns, Map<String, SearchResponseV2> fsnToSearchResponse ) {
        log.info("Fetching zulu data for search requirements");
        RetailProductAttributeResponse retailProductAttributeResponse = zuluClient.getRetailProductAttributes(zuluFsns);
        retailProductAttributeResponse.getEntityViews().forEach(entityView -> {
            try {
                String fsn = entityView.getEntityId();
                SearchResponseV2 searchResponse = fsnToSearchResponse.get(fsn);
                Map<String, String> analyticalInfo = (Map<String, String>) entityView.getView().get("analytics_info");
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
                searchResponse.setAnalyticalVertical(vertical);
                searchResponse.setCategory(category);
                searchResponse.setSuperCategory(superCategory);
                searchResponse.setTitle(title);
                searchResponse.setFsp(fsp);
                searchResponse.setBrand(brand);

                ProductInfo productInfo = new ProductInfo();
                productInfo.setFsn(fsn);
                productInfo.setBrand(brand);
                productInfo.setCategory(category);
                productInfo.setFsp(fsp);
                productInfo.setSuperCategory(superCategory);
                productInfo.setTitle(title);
                productInfo.setVertical(vertical);
                productInfoRepository.persist(productInfo);
            } catch (Exception e) {
                log.error("Error in fetching data from zulu " + e);
            }} );
    }

    private Set<String> fetchDataFromProductInfo(Map<String, SearchResponseV2> fsnToSearchResponse ) {
        log.info("Fetching product info data from db for search requirements");
        Set<String> fsns = fsnToSearchResponse.keySet();
        List<ProductInfo> productInfo = productInfoRepository.getProductInfo(fsns);
        Set<String> cachedFsns = Sets.newHashSet();
        productInfo.stream().forEach(pi -> {
            cachedFsns.add(pi.getFsn());
            SearchResponseV2 searchResponse = fsnToSearchResponse.get(pi.getFsn());
            searchResponse.setAnalyticalVertical(pi.getVertical());
            searchResponse.setCategory(pi.getCategory());
            searchResponse.setSuperCategory(pi.getSuperCategory());
            searchResponse.setTitle(pi.getTitle());
            searchResponse.setFsp(pi.getFsp());
            searchResponse.setBrand(pi.getBrand());
        });
        Set<String> fsnsCopy = Sets.newHashSet(fsns);
        fsnsCopy.removeAll(cachedFsns);
        return fsnsCopy;
    }

    protected boolean isEmptyString(String comment) {
        return comment == null || comment.trim().isEmpty() ? true : false;
    }


}
