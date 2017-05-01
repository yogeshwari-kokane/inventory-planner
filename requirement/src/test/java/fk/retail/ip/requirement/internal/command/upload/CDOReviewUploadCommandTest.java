package fk.retail.ip.requirement.internal.command.upload;

import com.google.common.collect.Lists;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.command.CalculateRequirementCommand;
import fk.retail.ip.requirement.internal.command.FdpRequirementIngestorImpl;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.TestHelper;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.UploadOverrideFailureLineItem;
import fk.retail.ip.ssl.model.SupplierSelectionResponse;
import fk.retail.ip.ssl.model.SupplierView;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.junit.Assert;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by agarwal.vaibhav on 03/03/17.
 */
@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class CDOReviewUploadCommandTest {

    @InjectMocks
    CDOReviewUploadCommand CDOReviewUploadCommand;

    @Mock
    FdpRequirementIngestorImpl fdpRequirementIngestor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void uploadTest() throws IOException {
        List<RequirementDownloadLineItem> requirementDownloadLineItems =
                TestHelper.getCdoReviewRequirementDownloadLineItem();
        List<Requirement> requirements = getRequirements();
        Map<String, String> fsnVerticalMap = getFsnVerticalMap();
        MultiKeyMap<String,SupplierSelectionResponse> fsnWhSupplierMap = getFsnWhSupplierMap();
        List<UploadOverrideFailureLineItem> uploadOverrideFailureLineItems = CDOReviewUploadCommand.execute(requirementDownloadLineItems ,requirements, "", fsnVerticalMap, fsnWhSupplierMap);

        Map<Long, Requirement> requirementMap = requirements.stream().collect
                (Collectors.toMap(Requirement::getId, Function.identity()));

        Assert.assertEquals(20, (int)requirementMap.get((long)1).getQuantity());
        Assert.assertEquals(100, (int)requirementMap.get((long)1).getApp());
        Assert.assertEquals("new_supplier", requirementMap.get((long)1).getSupplier());
        Assert.assertEquals(20, (int)requirementMap.get((long)1).getSla());

        Assert.assertEquals(100, (int)requirementMap.get((long)2).getQuantity());
        Assert.assertEquals(100, (int)requirementMap.get((long)3).getQuantity());
        Assert.assertEquals(4, (int)requirementMap.get((long)4).getSla());
        Assert.assertEquals(9, (int)requirementMap.get((long)5).getApp());

        Assert.assertEquals("new Supplier", requirementMap.get((long)6).getSupplier());
        Assert.assertEquals(20, (int)requirementMap.get((long)6).getSla());

        Assert.assertEquals(4, uploadOverrideFailureLineItems.size());
        Assert.assertEquals(Constants.SUGGESTED_QUANTITY_IS_NOT_GREATER_THAN_ZERO,
                uploadOverrideFailureLineItems.get(0).getFailureReason());
        Assert.assertEquals(Constants.QUANTITY_OVERRIDE_COMMENT_IS_MISSING,
                uploadOverrideFailureLineItems.get(1).getFailureReason());
        Assert.assertEquals(Constants.SUPPLIER_OVERRIDE_COMMENT_IS_MISSING
                + System.lineSeparator() + Constants.SLA_QUANTITY_IS_NOT_GREATER_THAN_ZERO,
                uploadOverrideFailureLineItems.get(2).getFailureReason());
        Assert.assertEquals(Constants.INVALID_APP_WITHOUT_COMMENT,
                uploadOverrideFailureLineItems.get(3).getFailureReason());

    }


    private List<Requirement> getRequirements() {

        RequirementSnapshot snapshot = TestHelper.getRequirementSnapshot("[1,2]", 2, 3, 4, 5, 6);

        RequirementSnapshot snapshot1 = TestHelper.getRequirementSnapshot("[3,4]",7,8,9,10,11);

        List<Requirement> requirements = Lists.newArrayList();

        Requirement requirement = TestHelper.getRequirement(
                "fsn",
                "dummy_warehouse_1",
                RequirementApprovalState.CDO_REVIEW.toString(),
                true,
                snapshot,
                100,
                "ABC",
                100,
                101,
                "INR",
                3,
                "",
                "Daily planning"
        );
        requirement.setId((long) 1);
        requirements.add(requirement);

        requirement = TestHelper.getRequirement(
                "fsn",
                "dummy_warehouse_2",
                RequirementApprovalState.CDO_REVIEW.toString(),
                true,
                snapshot1,
                100,
                "DEF",
                10,
                9,
                "USD",
                4,
                "",
                "Daily planning"
        );
        requirement.setId((long) 2);
        requirements.add(requirement);

        requirement = TestHelper.getRequirement(
                "fsn_1",
                "dummy_warehouse_1",
                RequirementApprovalState.CDO_REVIEW.toString(),
                true,
                snapshot1,
                100,
                "DEF",
                10,
                9,
                "USD",
                4,
                "",
                "Daily planning"
        );
        requirement.setId((long) 3);
        requirements.add(requirement);

        requirement = TestHelper.getRequirement(
                "fsn_1",
                "dummy_warehouse_2",
                RequirementApprovalState.CDO_REVIEW.toString(),
                true,
                snapshot1,
                100,
                "DEF",
                10,
                9,
                "USD",
                4,
                "",
                "Daily planning"
        );
        requirement.setId((long) 4);
        requirements.add(requirement);

        requirement = TestHelper.getRequirement(
                "fsn_2",
                "dummy_warehouse_1",
                RequirementApprovalState.CDO_REVIEW.toString(),
                true,
                snapshot1,
                100,
                "DEF",
                10,
                9,
                "USD",
                4,
                "",
                "Daily planning"
        );
        requirement.setId((long) 5);
        requirements.add(requirement);

        requirement = TestHelper.getRequirement(
                "fsn_2",
                "dummy_warehouse_2",
                RequirementApprovalState.CDO_REVIEW.toString(),
                true,
                snapshot1,
                100,
                "DEF",
                10,
                9,
                "USD",
                4,
                "",
                "Daily planning"
        );
        requirement.setId((long) 6);
        requirements.add(requirement);

        return requirements;
    }

    Map<String,String> getFsnVerticalMap() {
        return new HashMap<>();
    }

    MultiKeyMap<String,SupplierSelectionResponse> getFsnWhSupplierMap() {
        MultiKeyMap<String, SupplierSelectionResponse> fsnWhSupplierMap = new MultiKeyMap<>();
        SupplierSelectionResponse supplierSelectionResponse = new SupplierSelectionResponse();
        SupplierView supplier = new SupplierView();
        supplier.setSla(5);
        supplier.setApp(150);
        supplier.setFullName("new_supplier");
        supplier.setMrp(200);
        supplier.setName("new_s");
        supplierSelectionResponse.setFsn("fsn");
        supplierSelectionResponse.setWarehouseId("dummy_warehouse_1");
        List<SupplierView> suppliers = Lists.newArrayList(supplier);
        supplierSelectionResponse.setSuppliers(suppliers);
        fsnWhSupplierMap.put("fsn", "dummy_warehouse_1", supplierSelectionResponse);

        SupplierSelectionResponse supplierSelectionResponse2 = new SupplierSelectionResponse();
        SupplierView supplier2 = new SupplierView();
        supplier2.setSla(5);
        supplier2.setApp(150);
        supplier2.setFullName("new Supplier");
        supplier2.setMrp(200);
        supplier2.setName("new_s2");
        supplierSelectionResponse2.setFsn("fsn_2");
        supplierSelectionResponse2.setWarehouseId("dummy_warehouse_2");
        List<SupplierView> suppliers2 = Lists.newArrayList(supplier2);
        supplierSelectionResponse2.setSuppliers(suppliers2);
        fsnWhSupplierMap.put("fsn_2", "dummy_warehouse_2", supplierSelectionResponse2);

        return fsnWhSupplierMap;
    }

}


