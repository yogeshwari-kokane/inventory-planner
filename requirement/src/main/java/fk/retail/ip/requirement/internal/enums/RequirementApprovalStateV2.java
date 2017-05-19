package fk.retail.ip.requirement.internal.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yogeshwari.k on 19/05/17.
 */
public enum RequirementApprovalStateV2 {

    PROPOSED(Arrays.asList("verified", "proposed"), "proposed"),
    CDO_REVIEW(Arrays.asList("approved"), "cdo_review"),
    BIZFIN_REVIEW(Arrays.asList("bd_approved"), "bizfin_review"),
    IPC_REVIEW(Arrays.asList("bizfin_approved"), "ipc_review"),
    IPC_FINALISED(Arrays.asList("ipc_finalized"), "ipc_finalized"),
    PUSHED_TO_PROC(Arrays.asList("closed"), "closed"),
    ERROR(Arrays.asList("error"), "error");


    private List<String> oldState;
    private String state;

    RequirementApprovalStateV2(List<String> oldState, String state) {
        this.oldState = oldState;
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

    public static List<String> getOldState(String state) {
        for (RequirementApprovalStateV2 approvalState : RequirementApprovalStateV2.values()) {
            if (approvalState.state.equalsIgnoreCase(state)) {
                return approvalState.oldState;
            }
        }
        return null;
    }


}
