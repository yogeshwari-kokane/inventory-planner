package fk.retail.ip.requirement.internal.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yogeshwari.k on 19/05/17.
 */
public enum RequirementApprovalStateV2 {

    PROPOSED("proposed"),
    CDO_REVIEW("cdo_review"),
    BIZFIN_REVIEW("bizfin_review"),
    IPC_REVIEW("ipc_review"),
    IPC_FINALISED("ipc_finalized"),
    PUSHED_TO_PROC("closed"),
    ERROR("error");

    private String state;

    RequirementApprovalStateV2(String state) {
        this.state = state;
    }

    @Override
    public String toString() { return this.state;}

    public static RequirementApprovalStateV2 fromString(String state) {
        for (RequirementApprovalStateV2 approvalState : RequirementApprovalStateV2.values()) {
            if (approvalState.state.equalsIgnoreCase(state)) {
                return approvalState;
            }
        }
        return null;
    }

}
