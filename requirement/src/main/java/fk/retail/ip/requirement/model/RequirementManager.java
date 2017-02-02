package fk.retail.ip.requirement.model;

import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.RequirementState;
import java.util.List;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class RequirementManager {

    private List<Requirement> requirements;

    public StreamingOutput download(String downloadState, boolean isLastAppSupplierRequired) {
        RequirementState requirementState = RequirementState.valueOf(downloadState);
        StreamingOutput output = requirementState.getDownloadCommand()
                .execute(requirements, isLastAppSupplierRequired);
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
