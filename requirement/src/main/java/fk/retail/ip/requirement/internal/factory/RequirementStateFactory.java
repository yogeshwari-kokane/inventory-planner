package fk.retail.ip.requirement.internal.factory;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.states.ProposedRequirementState;
import fk.retail.ip.requirement.internal.states.RequirementState;

/**
 * Created by nidhigupta.m on 30/01/17.
 */

public class RequirementStateFactory {

    private final ProposedRequirementState proposedRequirementState;

    @Inject
    public RequirementStateFactory(ProposedRequirementState proposedRequirementState) {
        this.proposedRequirementState = proposedRequirementState;
    }

    public  RequirementState getState(String state) {
        if (state.equals("proposed")) {
            return proposedRequirementState ;
        }
        return null;
    }

}
