package fk.retail.ip.requirement.internal.factory;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.enums.ApprovalState;
import fk.retail.ip.requirement.internal.states.BizFinReviewRequirementState;
import fk.retail.ip.requirement.internal.states.CDOReviewRequirementState;
import fk.retail.ip.requirement.internal.states.IPCFinalisedRequirementState;
import fk.retail.ip.requirement.internal.states.IPCReviewRequirementState;
import fk.retail.ip.requirement.internal.states.ProposedRequirementState;
import fk.retail.ip.requirement.internal.states.RequirementState;

/**
 * Created by nidhigupta.m on 30/01/17.
 */

public class RequirementStateFactory {

    private final ProposedRequirementState proposedRequirementState;
    private final CDOReviewRequirementState cdoReviewRequirementState;
    private final BizFinReviewRequirementState bizFinReviewRequirementState;
    private final IPCReviewRequirementState ipcReviewRequirementState;
    private final IPCFinalisedRequirementState ipcFinalisedRequirementState;

    @Inject
    public RequirementStateFactory(ProposedRequirementState proposedRequirementState,
                                   CDOReviewRequirementState cdoReviewRequirementState,
                                   BizFinReviewRequirementState bizFinReviewRequirementState,
                                   IPCReviewRequirementState ipcReviewRequirementState,
                                   IPCFinalisedRequirementState ipcFinalisedRequirementState) {
        this.proposedRequirementState = proposedRequirementState;
        this.cdoReviewRequirementState = cdoReviewRequirementState;
        this.bizFinReviewRequirementState = bizFinReviewRequirementState;
        this.ipcReviewRequirementState = ipcReviewRequirementState;
        this.ipcFinalisedRequirementState = ipcFinalisedRequirementState;
    }

    public  RequirementState getState(String state) {
        if (state.equals(ApprovalState.proposed.name())) {
            return proposedRequirementState ;
        } if (state.equals(ApprovalState.CDOReview.name())) {
            return cdoReviewRequirementState;
        } if (state.equals(ApprovalState.BizFinReview.name())) {
            return bizFinReviewRequirementState;
        } if (state.equals(ApprovalState.IPCReview.name())) {
            return  ipcReviewRequirementState;
        } if (state.equals(ApprovalState.IPCFinalised.name())) {
            return ipcFinalisedRequirementState;
        }
        return null;
    }

}
