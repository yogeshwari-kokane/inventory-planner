package fk.retail.ip.requirement.model;

import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.factory.RequirementStateFactory;
import fk.retail.ip.requirement.internal.states.RequirementState;
import java.util.List;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 26/01/17.
 */


public class RequirementManager {

    private List<Requirement> requirements;
    private final RequirementStateFactory requirementStateFactory;

    public RequirementManager(RequirementStateFactory requirementStateFactory) {
        this.requirementStateFactory = requirementStateFactory;
    }

    public StreamingOutput download(String downloadState, boolean isLastAppSupplierRequired) {
        RequirementState requirementState = requirementStateFactory.getState(downloadState);
        StreamingOutput output = requirementState.download(requirements, isLastAppSupplierRequired, downloadState);
        return output;
    }

    public void upload() {

    }

    public void calculate() {

    }



    public RequirementManager withRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
        return this;
    }

}
