package fk.retail.ip.requirement.model;

import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.RequirementState;

import java.util.List;
import java.util.Map;
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

    public List<RequirementUploadLineItem> upload(String uploadState, List<Map<String, Object>> parsedJson) {
        RequirementState requirementState = RequirementState.valueOf(uploadState);
        if (requirementState == null) {
            //log => unknown state
            return null;
        }
        return requirementState.getUploadCommand().execute(parsedJson, requirements);

    }

    public void calculate() {

    }

    public RequirementManager withRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
        return this;
    }

}
