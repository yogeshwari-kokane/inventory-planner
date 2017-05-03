package fk.retail.ip.requirement.internal.command.upload;

import com.google.common.collect.Lists;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.command.FdpRequirementIngestorImpl;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementEventLog;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.enums.EventType;
import fk.retail.ip.requirement.internal.enums.OverrideKey;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.RequirementEventLogRepository;
import fk.retail.ip.requirement.internal.repository.TestHelper;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.UploadOverrideFailureLineItem;
import org.junit.Assert;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import java.io.IOException;
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

    @Mock
    RequirementEventLogRepository requirementEventLogRepository;

    @Captor
    private ArgumentCaptor<List<RequirementEventLog>> argumentCaptor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void uploadTest() throws IOException {
        List<RequirementDownloadLineItem> requirementDownloadLineItems =
                TestHelper.getCdoReviewRequirementDownloadLineItem();
        List<Requirement> requirements = getRequirements();
        List<UploadOverrideFailureLineItem> uploadOverrideFailureLineItems = CDOReviewUploadCommand.execute(requirementDownloadLineItems ,requirements, "");

        Mockito.verify(requirementEventLogRepository).persist(argumentCaptor.capture());

        Map<String, Requirement> requirementMap = requirements.stream().collect
                (Collectors.toMap(Requirement::getId, Function.identity()));

        Assert.assertEquals(20, (int)requirementMap.get("1").getQuantity());
        Assert.assertEquals(100, (int)requirementMap.get("1").getApp());
        Assert.assertEquals("new_supplier", requirementMap.get("1").getSupplier());
        Assert.assertEquals(20, (int)requirementMap.get("1").getSla());

        Assert.assertEquals(100, (int)requirementMap.get("2").getQuantity());
        Assert.assertEquals(100, (int)requirementMap.get("3").getQuantity());
        Assert.assertEquals(4, (int)requirementMap.get("4").getSla());
        Assert.assertEquals(9, (int)requirementMap.get("5").getApp());

        Assert.assertEquals("new Supplier", requirementMap.get("6").getSupplier());
        Assert.assertEquals(20, (int)requirementMap.get("6").getSla());

        Assert.assertEquals(4, uploadOverrideFailureLineItems.size());
        Assert.assertEquals(Constants.SUGGESTED_QUANTITY_IS_NOT_GREATER_THAN_ZERO,
                uploadOverrideFailureLineItems.get(0).getFailureReason());
        Assert.assertEquals(Constants.QUANTITY_OVERRIDE_COMMENT_IS_MISSING,
                uploadOverrideFailureLineItems.get(1).getFailureReason());
        Assert.assertEquals(Constants.SLA_QUANTITY_IS_NOT_GREATER_THAN_ZERO
                 + System.lineSeparator() + Constants.SUPPLIER_OVERRIDE_COMMENT_IS_MISSING,
                uploadOverrideFailureLineItems.get(2).getFailureReason());
        Assert.assertEquals(Constants.INVALID_APP_WITHOUT_COMMENT,
                uploadOverrideFailureLineItems.get(3).getFailureReason());


        Assert.assertEquals("100.0", argumentCaptor.getValue().get(0).getOldValue());
        Assert.assertEquals("20", argumentCaptor.getValue().get(0).getNewValue());
        Assert.assertEquals(OverrideKey.QUANTITY.toString(), argumentCaptor.getValue().get(0).getAttribute());
        Assert.assertEquals("test_cdo_quantity", argumentCaptor.getValue().get(0).getReason());
        Assert.assertEquals(EventType.OVERRIDE.toString(), argumentCaptor.getValue().get(0).getEventType());

        Assert.assertEquals("101", argumentCaptor.getValue().get(2).getOldValue());
        Assert.assertEquals("100", argumentCaptor.getValue().get(2).getNewValue());
        Assert.assertEquals(OverrideKey.APP.toString(), argumentCaptor.getValue().get(2).getAttribute());
        Assert.assertEquals("test_cdo_price", argumentCaptor.getValue().get(2).getReason());

        Assert.assertEquals("ABC", argumentCaptor.getValue().get(3).getOldValue());
        Assert.assertEquals("new_supplier", argumentCaptor.getValue().get(3).getNewValue());
        Assert.assertEquals(OverrideKey.SUPPLIER.toString(), argumentCaptor.getValue().get(3).getAttribute());
        Assert.assertEquals("test_cdo_supplier", argumentCaptor.getValue().get(3).getReason());

        Assert.assertEquals("3", argumentCaptor.getValue().get(1).getOldValue());
        Assert.assertEquals("20", argumentCaptor.getValue().get(1).getNewValue());
        Assert.assertEquals(OverrideKey.SLA.toString(), argumentCaptor.getValue().get(1).getAttribute());
        Assert.assertEquals("Sla overridden by CDO", argumentCaptor.getValue().get(1).getReason());
        Assert.assertEquals(EventType.OVERRIDE.toString(), argumentCaptor.getValue().get(0).getEventType());

        Assert.assertEquals("4", argumentCaptor.getValue().get(4).getOldValue());
        Assert.assertEquals("20", argumentCaptor.getValue().get(4).getNewValue());
        Assert.assertEquals(OverrideKey.SLA.toString(), argumentCaptor.getValue().get(4).getAttribute());

        Assert.assertEquals("DEF", argumentCaptor.getValue().get(5).getOldValue());
        Assert.assertEquals("new Supplier", argumentCaptor.getValue().get(5).getNewValue());
        Assert.assertEquals(OverrideKey.SUPPLIER.toString(), argumentCaptor.getValue().get(5).getAttribute());

        Assert.assertEquals(6, argumentCaptor.getValue().size());

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
        requirement.setId("1");
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
        requirement.setId("2");
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
        requirement.setId("3");
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
        requirement.setId("4");
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
        requirement.setId("5");
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
        requirement.setId("6");
        requirements.add(requirement);

        return requirements;
    }

}


