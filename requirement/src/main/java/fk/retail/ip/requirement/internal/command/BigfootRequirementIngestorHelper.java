package fk.retail.ip.requirement.internal.command;

import fk.retail.ip.bigfoot.model.*;
import fk.retail.ip.requirement.model.RequirementChangeRequest;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.model.RequirementEntityMapper;
import fk.retail.ip.requirement.model.RequirementEventMapper;

import java.util.List;
import fk.retail.ip.bigfoot.internal.command.BigfootRequirementIngestor;
/**
 * Created by yogeshwari.k on 22/03/17.
 */
public class BigfootRequirementIngestorHelper {
    BatchBigfootRequirementEventEntityPayload batchBigfootRequirementEventEntityPayload;
    RequirementEntityMapper requirementEntityMapper;
    RequirementEventMapper requirementEventMapper;
    BigfootRequirementIngestor bigfootRequirementIngestor;

    public BatchBigfootRequirementEventEntityPayload pushToBigfoot(List<RequirementChangeRequest> requirementChangeRequests){
        requirementChangeRequests.forEach(req -> {
            String requirementId= getRequirementId(req.getRequirement());
            RequirementEntityPayload requirementEntityPayload = requirementEntityMapper.convertRequirementToEntityPayload(requirementId,req.getRequirement());
            List<RequirementEventPayload> requirementEventPayload = requirementEventMapper.convertRequirementToEventPayload(requirementId,req.getChangeMaps());
            batchBigfootRequirementEventEntityPayload.getRequirementEntityPayloads().add(requirementEntityPayload);
            batchBigfootRequirementEventEntityPayload.getRequirementEventPayloads().addAll(requirementEventPayload);
            //bigfootRequirementIngestor.pushToBigfoot(batchBigfootRequirementEventEntityPayload);
        });

        return batchBigfootRequirementEventEntityPayload;
    }

    private String getRequirementId(Requirement requirement) {
        String requirementId = requirement.getFsn()+requirement.getWarehouse()+(requirement.getCreatedAt().toString());
        return requirementId;
    }

}
