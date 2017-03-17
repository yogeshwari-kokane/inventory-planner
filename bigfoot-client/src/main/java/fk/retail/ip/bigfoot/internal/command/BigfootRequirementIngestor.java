package fk.retail.ip.bigfoot.internal.command;

import fk.retail.ip.bigfoot.model.*;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.model.ChangeMap;
import fk.retail.ip.requirement.model.RequirementChangeRequest;
import java.util.List;
/**
 * Created by yogeshwari.k on 17/03/17.
 */
public class BigfootRequirementIngestor {
    BatchBigfootRequirementEventEntityPayload batchBigfootRequirementEventEntityPayload;
    RequirementEntityMapper requirementEntityMapper;
    RequirementEventMapper requirementEventMapper;
    public void pushToBigfoot(List<RequirementChangeRequest> requirementChangeRequests) {
        requirementChangeRequests.forEach(req -> {
            String requirementId= getRequirementId(req.getRequirement());
            RequirementEntityPayload requirementEntityPayload = requirementEntityMapper.convertRequirementToEntityPayload(requirementId,req.getRequirement());
            List<RequirementEventPayload> requirementEventPayload = requirementEventMapper.convertRequirementToEventPayload(requirementId,req.getChangeMaps());
            batchBigfootRequirementEventEntityPayload.getRequirementEntityPayloads().add(requirementEntityPayload);
            batchBigfootRequirementEventEntityPayload.getRequirementEventPayloads().addAll(requirementEventPayload);
        });
    }

    private String getRequirementId(Requirement requirement) {
        String requirementId = requirement.getFsn()+requirement.getWarehouse()+(requirement.getCreatedAt().toString());
        return requirementId;
    }
}
