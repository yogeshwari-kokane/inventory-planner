package fk.retail.ip.projection.internal.enums;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

/**
 * Created by nidhigupta.m on 08/01/17.
 */

@Getter
public enum ProjectionApprovalState {
    APPROVED ("approved"),
    VERIFIED ("verified"),
    BD_APPROVED ("bd_approved");

    private String displayName;


    ProjectionApprovalState(String displayName) {
        this.displayName = displayName;
    }

    private static final List<String> validOverrideState = Arrays.asList(APPROVED.getDisplayName(), VERIFIED.getDisplayName(), BD_APPROVED.getDisplayName());

    public static boolean isValidOverrideState(String state) {
        return validOverrideState.contains(state);
    }
}
