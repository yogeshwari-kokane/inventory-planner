package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import fk.retail.ip.requirement.internal.entities.*;

import java.util.*;

import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.RequirementUploadLineItem;
import fk.retail.ip.zulu.internal.entities.EntityView;
import fk.retail.ip.zulu.internal.entities.RetailProductAttributeResponse;
import org.bouncycastle.cert.ocsp.Req;

public class TestHelper {

    public static IwtRequestItem getIwtRequestItem(String fsn, String status, IwtRequest iwtRequest) {
        IwtRequestItem iwtRequestItem = new IwtRequestItem();
        iwtRequestItem.setFsn(fsn);
        iwtRequestItem.setStatus(status);
        iwtRequestItem.setCreatedAt(new Date());
        iwtRequestItem.setIwtRequest(iwtRequest);
        return iwtRequestItem;
    }

    public static IwtRequest getIwtRequest(String externalId) {
        IwtRequest iwtRequest = new IwtRequest();
        iwtRequest.setExternalId(externalId);
        iwtRequest.setCreatedAt(new Date());
        return iwtRequest;
    }

    public static GroupFsn getGroupFsn(String fsn, Group group) {
        GroupFsn groupFsn = new GroupFsn();
        groupFsn.setFsn(fsn);
        groupFsn.setGroup(group);
        groupFsn.setCreatedAt(new Date());
        return groupFsn;
    }

    public static Group getGroup(String name) {
        Group group = new Group();
        group.setName(name);
        group.setCreatedAt(new Date());
        return group;
    }

    public static OpenRequirementAndPurchaseOrder getOpenRequirementAndPurchaseOrder() {
        OpenRequirementAndPurchaseOrder openRequirementAndPurchaseOrder = new OpenRequirementAndPurchaseOrder();
        openRequirementAndPurchaseOrder.setFsn("fsn1");
        openRequirementAndPurchaseOrder.setCreatedAt(new Date());
        return openRequirementAndPurchaseOrder;
    }

    public static Policy getPolicy(String fsn, Group group) {
        Policy policy = new Policy();
        policy.setFsn(fsn);
        policy.setGroup(group);
        return policy;
    }

    public static FsnBand getFsnBand(String fsn, String timeFrame) {
        FsnBand fsnBand = new FsnBand();
        fsnBand.setFsn(fsn);
        fsnBand.setSalesBand(2);
        fsnBand.setPvBand(3);
        fsnBand.setTimeFrame(timeFrame);
        return fsnBand;
    }

    public static LastAppSupplier getLastAppSupplier(String fsn, String wh,String supplier, int app ) {
        LastAppSupplier lastAppSupplier = new LastAppSupplier();
        lastAppSupplier.setFsn(fsn);
        lastAppSupplier.setWarehouse(wh);
        lastAppSupplier.setLastSupplier(supplier);
        lastAppSupplier.setLastApp(app);
        return lastAppSupplier;
    }

    public static WeeklySale getWeeklySale(String fsn, String wh, int week, int saleQuantity) {
        WeeklySale weeklySale = new WeeklySale();
        weeklySale.setFsn(fsn);
        weeklySale.setWarehouse(wh);
        weeklySale.setWeek(week);
        weeklySale.setSaleQty(saleQuantity);
        return weeklySale;
    }

    public static Requirement getRequirement(String fsn, String wh, String state, boolean enabled,
                                             RequirementSnapshot snapshot, int quantity, String supplier,
                                             int mrp, int app, String currency, int sla,
                                             String comment, String procType) {
        Requirement requirement = new Requirement();
        requirement.setFsn(fsn);
        requirement.setState(state);
        requirement.setEnabled(enabled);
        requirement.setWarehouse(wh);
        requirement.setRequirementSnapshot(snapshot);
        requirement.setQuantity(quantity);
        requirement.setSupplier(supplier);
        requirement.setMrp(mrp);
        requirement.setApp(app);
        requirement.setCurrency(currency);
        requirement.setSla(sla);
        requirement.setOverrideComment(comment);
        requirement.setProcType(procType);
        return requirement;
    }

    public static RequirementSnapshot getRequirementSnapshot(String forecast, int inventory, int qoh, int po , int req, int intransit) {
        RequirementSnapshot snapshot = new RequirementSnapshot();
        snapshot.setForecast(forecast);
        snapshot.setInventoryQty(inventory);
        snapshot.setQoh(qoh);
        snapshot.setPendingPoQty(po);
        snapshot.setOpenReqQty(req);
        snapshot.setIwitIntransitQty(intransit);
        return snapshot;
    }

    public static Warehouse getWarehouse(String whCode, String whName) {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseCode(whCode);
        warehouse.setWarehouseName(whName);
        return warehouse;
    }

     /*
    * Initialise mocked zulu response
    * */
    public static RetailProductAttributeResponse getZuluData() {
        RetailProductAttributeResponse retailProductAttributeResponse = new RetailProductAttributeResponse();
        EntityView entityView = new EntityView();
        List<EntityView> entityViews = Lists.newArrayList();
        Map<Object, Object> view = new HashMap<>();
        Map<String, String> analyticalInfo = new HashMap<>();
        Map<Object, Object> supplyChainAttributes = new HashMap<>();
        entityView.setEntityId("fsn");
        supplyChainAttributes.put("procurement_title", "dummy_zulu_title");
        Map<String, String> productAttributes = new HashMap<>();
        analyticalInfo.put("vertical", "dummy_zulu_vertical");
        analyticalInfo.put("category", "dummy_zulu_category");
        analyticalInfo.put("super_category", "dummy_zulu_super_category");
        productAttributes.put("brand", "dummy_zulu_brand");
        productAttributes.put("flipkart_selling_price", String.valueOf(2));
        supplyChainAttributes.put("product_attributes", productAttributes);
        view.put("analytics_info", analyticalInfo);
        view.put("supply_chain", supplyChainAttributes);
        entityView.setView(view);
        entityViews.add(entityView);
        retailProductAttributeResponse.setEntityViews(entityViews);
        return retailProductAttributeResponse;
    }

    /*
    * Initialiase mocked db response for getting product info
    * */
    public static List<ProductInfo> getProductInfo() {
        List<ProductInfo> productInfoList = Lists.newArrayList();
        ProductInfo dbProductInfo = new ProductInfo();
        dbProductInfo.setFsn("fsn");
        dbProductInfo.setVertical("dummy_db_vertical");
        dbProductInfo.setSuperCategory("dummy_db_super_category");
        dbProductInfo.setCategory("dummy_db_category");
        dbProductInfo.setBrand("dummy_db_brand");
        dbProductInfo.setTitle("dummy_db_title");
        dbProductInfo.setFsp(1);
        productInfoList.add(dbProductInfo);

        return productInfoList;
    }


    public static List<RequirementDownloadLineItem> getProposedRequirementDownloadLineItem() {
        List<RequirementDownloadLineItem> requirementDownloadLineItems = new ArrayList<>();

        /*This one has all fields up-to-date*/
        RequirementDownloadLineItem requirementDownloadLineItem = new RequirementDownloadLineItem();
        //requirementDownloadLineItem.setRequirementId((long)12);
        /*Quantity should be overridden*/
        requirementDownloadLineItem.setWarehouseName("dummy_warehouse_1");
        requirementDownloadLineItem.setFsn("dummy_fsn");
        requirementDownloadLineItem.setRequirementId((long)1);
        requirementDownloadLineItem.setQuantity(100);
        requirementDownloadLineItem.setIpcQuantityOverride(20);
        requirementDownloadLineItem.setIpcQuantityOverrideReason("test_ipc");
        requirementDownloadLineItem.setCdoQuantityOverride(15);
        requirementDownloadLineItem.setCdoOverrideReason("test_cdo_quantity");
        requirementDownloadLineItem.setCdoPriceOverride(100);
        requirementDownloadLineItem.setCdoPriceOverrideReason("test_cdo_price");
        requirementDownloadLineItem.setCdoSupplierOverride("new_supplier");
        requirementDownloadLineItems.add(requirementDownloadLineItem);

        /*Override should fail for this as comment is missing*/
        RequirementDownloadLineItem requirementDownloadLineItem2 = new RequirementDownloadLineItem();
        requirementDownloadLineItem2.setWarehouseName("dummy_warehouse_2");
        requirementDownloadLineItem2.setFsn("dummy_fsn");
        requirementDownloadLineItem2.setRequirementId((long)2);
        requirementDownloadLineItem2.setQuantity(100);
        requirementDownloadLineItem2.setIpcQuantityOverride(20);
        requirementDownloadLineItems.add(requirementDownloadLineItem2);

        /*Override should fail as warehouse is missing*/
        RequirementDownloadLineItem requirementDownloadLineItem1 = new RequirementDownloadLineItem();
        requirementDownloadLineItem1.setFsn("dummy_fsn_1");
        requirementDownloadLineItem1.setRequirementId((long)3);
        requirementDownloadLineItem1.setIpcQuantityOverride(20);
        requirementDownloadLineItems.add(requirementDownloadLineItem1);

        /*Override should fail as quantity is not a positive integer*/
        RequirementDownloadLineItem requirementDownloadLineItem3 = new RequirementDownloadLineItem();
        requirementDownloadLineItem3.setFsn("dummy_fsn_1");
        requirementDownloadLineItem3.setWarehouseName("dummy_warehouse");
        requirementDownloadLineItem3.setQuantity(100);
        requirementDownloadLineItem3.setRequirementId((long)4);
        requirementDownloadLineItem3.setIpcQuantityOverride(0);
        requirementDownloadLineItem3.setIpcQuantityOverrideReason("test_ipc");
        requirementDownloadLineItems.add(requirementDownloadLineItem3);

        return requirementDownloadLineItems;
    }

    public static List<RequirementDownloadLineItem> getCDOReviewRequirementDownloadLineItem() {
        List<RequirementDownloadLineItem> requirementDownloadLineItems = new ArrayList<>();

        /*Quantity, app, supplier, sla should be overridden with comment*/
        RequirementDownloadLineItem requirementDownloadLineItem = new RequirementDownloadLineItem();
        requirementDownloadLineItem.setWarehouseName("dummy_warehouse_1");
        requirementDownloadLineItem.setFsn("fsn");
        requirementDownloadLineItem.setCdoQuantityOverride(20);
        requirementDownloadLineItem.setCdoQuantityOverrideReason("test_cdo_quantity");
        requirementDownloadLineItem.setCdoPriceOverride(100);
        requirementDownloadLineItem.setCdoPriceOverrideReason("test_cdo_price");
        requirementDownloadLineItem.setCdoSupplierOverride("new_supplier");
        requirementDownloadLineItem.setCdoSupplierOverrideReason("test_cdo_supplier");
        requirementDownloadLineItem.setNewSla(20);
        requirementDownloadLineItems.add(requirementDownloadLineItem);

        /*Quantity override should not happen*/
        RequirementDownloadLineItem requirementDownloadLineItem2 = new RequirementDownloadLineItem();
        requirementDownloadLineItem2.setWarehouseName("dummy_warehouse_2");
        requirementDownloadLineItem2.setFsn("fsn");
        requirementDownloadLineItem2.setCdoQuantityOverride(-1);
        requirementDownloadLineItem2.setCdoQuantityOverrideReason("test_cdo_quantity");
        requirementDownloadLineItems.add(requirementDownloadLineItem2);

        /*No override should happen as comment is missing*/
        RequirementDownloadLineItem requirementDownloadLineItem3 = new RequirementDownloadLineItem();
        requirementDownloadLineItem3.setWarehouseName("dummy_warehouse_1");
        requirementDownloadLineItem3.setFsn("fsn_1");
        requirementDownloadLineItem3.setCdoQuantityOverride(20);
        requirementDownloadLineItems.add(requirementDownloadLineItem3);

        /*No override should happen as sla < 0 and supplier override comment is absent*/
        RequirementDownloadLineItem requirementDownloadLineItem1 = new RequirementDownloadLineItem();
        requirementDownloadLineItem1.setFsn("fsn_1");
        requirementDownloadLineItem1.setWarehouseName("dummy_warehouse_2");
        requirementDownloadLineItem1.setNewSla(-1);
        requirementDownloadLineItem1.setCdoSupplierOverride("new_supplier");
        requirementDownloadLineItems.add(requirementDownloadLineItem1);

        /*No override as app quantity is less than zero and reason is missing*/
        RequirementDownloadLineItem requirementDownloadLineItem4 = new RequirementDownloadLineItem();
        requirementDownloadLineItem4.setFsn("fsn_2");
        requirementDownloadLineItem4.setWarehouseName("dummy_warehouse_1");
        requirementDownloadLineItem4.setCdoPriceOverride(1);
        requirementDownloadLineItem4.setCdoPriceOverrideReason("  ");
        requirementDownloadLineItems.add(requirementDownloadLineItem4);

        /*Supplier and sla should be overridden*/
        RequirementDownloadLineItem requirementDownloadLineItem5 = new RequirementDownloadLineItem();
        requirementDownloadLineItem5.setFsn("fsn_2");
        requirementDownloadLineItem5.setWarehouseName("dummy_warehouse_2");
        requirementDownloadLineItem5.setCdoSupplierOverride("new Supplier");
        requirementDownloadLineItem5.setCdoSupplierOverrideReason("test_cdo_supplier");
        requirementDownloadLineItem5.setNewSla(20);
        requirementDownloadLineItems.add(requirementDownloadLineItem5);


        return requirementDownloadLineItems;

    }

    public static List<RequirementDownloadLineItem> getBizfinReviewDownloadLineItem() {
        List<RequirementDownloadLineItem> requirementDownloadLineItems = new ArrayList<>();

        /*Quantity and comment should be overridden*/
        RequirementDownloadLineItem requirementDownloadLineItem = new RequirementDownloadLineItem();
        requirementDownloadLineItem.setWarehouseName("dummy_warehouse_1");
        requirementDownloadLineItem.setFsn("fsn");
        requirementDownloadLineItem.setBizFinRecommendedQuantity(20);
        requirementDownloadLineItem.setBizFinComment("test_bizfin");
        requirementDownloadLineItems.add(requirementDownloadLineItem);

        /*Override should fail as comment is missing*/
        RequirementDownloadLineItem requirementDownloadLineItem3 = new RequirementDownloadLineItem();
        requirementDownloadLineItem3.setWarehouseName("dummy_warehouse_2");
        requirementDownloadLineItem3.setFsn("fsn");
        requirementDownloadLineItem3.setBizFinRecommendedQuantity(20);
        requirementDownloadLineItems.add(requirementDownloadLineItem3);

        /*Override only the comment*/
        RequirementDownloadLineItem requirementDownloadLineItem1 = new RequirementDownloadLineItem();
        requirementDownloadLineItem1.setFsn("fsn_1");
        requirementDownloadLineItem1.setWarehouseName("dummy_warehouse_1");
        requirementDownloadLineItem1.setBizFinComment("test_bizfin");
        requirementDownloadLineItems.add(requirementDownloadLineItem1);

        /*Should impact nothing on upload */
        RequirementDownloadLineItem requirementDownloadLineItem2 = new RequirementDownloadLineItem();
        requirementDownloadLineItem2.setFsn("fsn_1");
        requirementDownloadLineItem2.setWarehouseName("dummy_warehouse_2");
        requirementDownloadLineItems.add(requirementDownloadLineItem2);


        return requirementDownloadLineItems;
    }

}
