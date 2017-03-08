package fk.retail.ip.requirement.internal.command.upload;

import com.google.common.collect.Lists;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.TestHelper;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.RequirementUploadLineItem;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by agarwal.vaibhav on 03/03/17.
 */

@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class BizFinReviewUploadCommandTest {

    @InjectMocks
    BizFinReviewUploadCommand bizFinReviewUploadCommand;

    @Mock
    RequirementRepository requirementRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void uploadTest() throws IOException {
        List<RequirementDownloadLineItem> requirementDownloadLineItems = TestHelper.getBizfinReviewDownloadLineItem();
        List<Requirement> requirements = getRequirements();
        List<RequirementUploadLineItem> requirementUploadLineItems = bizFinReviewUploadCommand.execute(requirementDownloadLineItems, requirements);
        ArgumentCaptor<Requirement> argumentCaptor = ArgumentCaptor.forClass(Requirement.class);
        Mockito.verify(requirementRepository,Mockito.times(2)).persist(argumentCaptor.capture());



        Assert.assertEquals(1, requirementUploadLineItems.size());
        Assert.assertEquals(20, (int)argumentCaptor.getAllValues().get(0).getQuantity());
        Assert.assertEquals("{\"quantityOverrideComment\":\"test_bizfin\"}", argumentCaptor.getAllValues().get(0).getOverrideComment());
        Assert.assertEquals("{\"quantityOverrideComment\":\"test_bizfin\"}", argumentCaptor.getAllValues().get(1).getOverrideComment());
    }

    private List<Requirement> getRequirements() {

        RequirementSnapshot snapshot = TestHelper.getRequirementSnapshot("[1,2]", 2, 3, 4, 5, 6);

        RequirementSnapshot snapshot1 = TestHelper.getRequirementSnapshot("[3,4]",7,8,9,10,11);

        List<Requirement> requirements = Lists.newArrayList();

        Requirement requirement = TestHelper.getRequirement("fsn", "dummy_warehouse_1", RequirementApprovalState.BIZFIN_REVIEW.toString(), true, snapshot , 21, "ABC",
                100, 101, "INR", 3, "", "Daily planning");
        requirement.setId((long) 1);
        requirements.add(requirement);

        requirement = TestHelper.getRequirement("fsn", "dummy_warehouse_2",RequirementApprovalState.BIZFIN_REVIEW.toString(), true, snapshot1 , 22, "DEF",
                10, 9, "USD", 4, "", "Daily planning");
        requirement.setId((long) 2);
        requirements.add(requirement);

        requirement = TestHelper.getRequirement("fsn_1", "dummy_warehouse_1",RequirementApprovalState.BIZFIN_REVIEW.toString(), true, snapshot1 , 22, "DEF",
                10, 9, "USD", 4, "", "Daily planning");
        requirement.setId((long) 3);
        requirements.add(requirement);

        requirement = TestHelper.getRequirement("fsn_1", "dummy_warehouse_2",RequirementApprovalState.BIZFIN_REVIEW.toString(), true, snapshot1 , 22, "DEF",
                10, 9, "USD", 4, "", "Daily planning");
        requirement.setId((long) 4);
        requirements.add(requirement);


        return requirements;
    }
}
