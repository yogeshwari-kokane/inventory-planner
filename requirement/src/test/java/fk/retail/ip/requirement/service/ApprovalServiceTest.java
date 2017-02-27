package fk.retail.ip.requirement.service;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.config.TestModule;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Pragalathan M<pragalathan.m@flipkart.com>
 */
@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class ApprovalServiceTest extends TransactionalJpaRepositoryTest {

    @Inject
    private RequirementRepository requirementRepository;

    @Test
    public void testProposedStateForwardFlow() {
        testForwardFlow("proposed", "verified", "verify");
    }

    @Test
    public void testVerifiedStateForwardFlow() {
        testForwardFlow("verified", "approved", "approve");
    }

    @Test
    public void testApprovedStateForwardFlow() {
        testForwardFlow("approved", "bd_approved", "bd_approve");
    }

    @Test
    public void testBdApprovedStateForwardFlow() {
        testForwardFlow("bd_approved", "biz_fin_approved", "biz_fin_approve");
    }

    @Test
    public void testBizFinApprovedStateForwardFlow() {
        testForwardFlow("biz_fin_approved", "ipc_finalized", "ipc_finalize");
    }

    @Test
    public void testVerifiedStateBackwardFlow() {
        testBackwardFlow("verified", "proposed", "cancel_verify");
    }

    @Test
    public void testApprovedStateBackwardFlow() {
        testBackwardFlow("approved", "verified", "cancel_approve");
    }

    @Test
    public void testBdApprovedStateBackwardFlow() {
        testBackwardFlow("bd_approved", "approved", "cancel_bd_approve");
    }

    @Test
    public void testBizFinApprovedStateBackwardFlow() {
        testBackwardFlow("biz_fin_approved", "bd_approved", "cancel_biz_fin_approve");
    }

    private void testForwardFlow(String fromState, String toState, String action) {
        Requirement requirement = createRequirement(fromState);
        ApprovalService service = new ApprovalService("/requirement-state-actions.json");
        Function<Requirement, String> getter = Requirement::getState;
        service.changeState(Arrays.asList(requirement), "userId", action, getter, new ApprovalService.CopyOnStateChangeAction(requirementRepository));

        List<Requirement> results = requirementRepository.find(Arrays.asList(requirement.getFsn()), toState);
        Requirement actual = results.get(0);
        Assert.assertEquals(toState, actual.getState());
        Assert.assertTrue(actual.isCurrent());
        Assert.assertTrue(actual.isEnabled());
        Assert.assertEquals(actual.getPreviousStateId(), requirement.getId());
        Assert.assertEquals(actual.getQuantity(), requirement.getQuantity());
        Assert.assertEquals(actual.getSla(), requirement.getSla());
        Assert.assertEquals(actual.getSupplier(), requirement.getSupplier());
        Assert.assertEquals(actual.getApp(), requirement.getApp());

        Assert.assertFalse(requirement.isCurrent());
        Assert.assertTrue(requirement.isEnabled());
    }

    private void testBackwardFlow(String fromState, String toState, String action) {
        createRequirement(toState); // create the previous state first
        Requirement requirement = createRequirement(fromState);
        ApprovalService service = new ApprovalService("/requirement-state-actions.json");
        Function<Requirement, String> getter = Requirement::getState;
        service.changeState(Arrays.asList(requirement), "userId", action, getter, new ApprovalService.CopyOnStateChangeAction(requirementRepository));

        List<Requirement> results = requirementRepository.find(Arrays.asList(requirement.getFsn()), toState);
        Requirement actual = results.get(0);
        Assert.assertEquals(toState, actual.getState());
        Assert.assertTrue(actual.isCurrent());
        Assert.assertTrue(actual.isEnabled());
//        Assert.assertEquals(actual.getPreviousStateId(), requirement.getId());
        Assert.assertEquals(actual.getQuantity(), requirement.getQuantity());
        Assert.assertEquals(actual.getSla(), requirement.getSla());
        Assert.assertEquals(actual.getSupplier(), requirement.getSupplier());
        Assert.assertEquals(actual.getApp(), requirement.getApp());

        Assert.assertFalse(requirement.isCurrent());
        Assert.assertTrue(requirement.isEnabled());
    }

    private Requirement createRequirement(String state) {
        Requirement requirement = new Requirement();
        requirement.setFsn("fsn1");
        requirement.setState(state);
        requirement.setEnabled(true);
        requirement.setCurrent(true);
        requirement.setWarehouse("dummy_warehouse");
        requirement.setCreatedAt(new Date());
        requirement.setUpdatedAt(new Date());

        requirementRepository.persist(requirement);
        return requirement;
    }
}
