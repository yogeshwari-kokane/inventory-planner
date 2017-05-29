package fk.retail.ip.requirement.internal.factory;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalStateV2;
import fk.retail.ip.requirement.internal.exception.InvalidRequirementStateException;
import fk.retail.ip.requirement.internal.states.*;

/**
 * Created by yogeshwari.k on 25/05/17.
 */
public class RequirementStateFactoryV2 {

    private final ProposedRequirementState proposedRequirementState;
    private final BizFinRequirementState bizFinRequirementState;
    private final CDOReviewRequirementState cdoReviewRequirementState;
    private final IPCReviewRequirementState ipcReviewRequirementState;
    private final IPCFinalisedRequirementState ipcFinalisedRequirementState;

    @Inject
    public RequirementStateFactoryV2(ProposedRequirementState proposedRequirementState, BizFinRequirementState bizFinRequirementState,
                                   CDOReviewRequirementState cdoReviewRequirementState, IPCReviewRequirementState ipcReviewRequirementState,
                                   IPCFinalisedRequirementState ipcFinalisedRequirementState) {
        this.proposedRequirementState = proposedRequirementState;
        this.cdoReviewRequirementState = cdoReviewRequirementState;
        this.bizFinRequirementState = bizFinRequirementState;
        this.ipcReviewRequirementState = ipcReviewRequirementState;
        this.ipcFinalisedRequirementState = ipcFinalisedRequirementState;
    }

    public RequirementState getRequirementState(String requirementState) throws  InvalidRequirementStateException {

        RequirementApprovalStateV2 requirementApprovalState = RequirementApprovalStateV2.fromString(requirementState);
        if (requirementApprovalState == null) {
            throw new InvalidRequirementStateException(requirementState + " is not a valid requirement state");

        }
        switch (requirementApprovalState) {
            case PROPOSED:
                return proposedRequirementState;
            case CDO_REVIEW:
                return cdoReviewRequirementState;
            case BIZFIN_REVIEW:
                return bizFinRequirementState;
            case IPC_REVIEW:
                return ipcReviewRequirementState;
            case IPC_FINALISED:
                return ipcFinalisedRequirementState;
        }

        return null;
    }

}
