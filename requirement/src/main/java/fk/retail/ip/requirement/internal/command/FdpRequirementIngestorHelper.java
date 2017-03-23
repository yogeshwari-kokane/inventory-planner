package fk.retail.ip.requirement.internal.command;

import fk.retail.ip.fdp.model.*;
import fk.retail.ip.requirement.model.ChangeMap;
import fk.retail.ip.requirement.model.RequirementChangeRequest;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.model.RequirementEntityMapper;
import fk.retail.ip.requirement.model.RequirementEventMapper;

import java.util.List;
import fk.retail.ip.fdp.internal.command.FdpRequirementIngestor;
/**
 * Created by yogeshwari.k on 22/03/17.
 */
public class FdpRequirementIngestorHelper {
    BatchFdpRequirementEventEntityPayload batchFdpRequirementEventEntityPayload;
    RequirementEntityMapper requirementEntityMapper;
    RequirementEventMapper requirementEventMapper;
    FdpRequirementIngestor fdpRequirementIngestor;

    public void pushToFdp(List<RequirementChangeRequest> requirementChangeRequests){
        requirementChangeRequests.forEach(req -> {
            String requirementId= getRequirementId(req.getRequirement());
            FdpRequirementEntityPayload fdpRequirementEntityPayload = requirementEntityMapper.convertRequirementToEntityPayload(requirementId,req.getRequirement());
            List<FdpRequirementEventPayload> fdpRequirementEventPayload = requirementEventMapper.convertRequirementToEventPayload(requirementId,req.getChangeMaps());
            batchFdpRequirementEventEntityPayload.getRequirementEntity().add(fdpRequirementEntityPayload);
            batchFdpRequirementEventEntityPayload.getRequirementEvent().addAll(fdpRequirementEventPayload);
            fdpRequirementIngestor.pushToFdp(batchFdpRequirementEventEntityPayload);
        });
    }

    public ChangeMap createChangeMap(String attribute, String oldValue, String newValue, String eventType, String reason, String user){
        ChangeMap changeMap = new ChangeMap();
        changeMap.setAttribute(attribute);
        changeMap.setOldValue(oldValue);
        changeMap.setNewValue(newValue);
        changeMap.setEventType(eventType);
        changeMap.setReason(reason);
        changeMap.setUser(user);
        return changeMap;
    }

    private String getRequirementId(Requirement requirement) {
        String requirementId = requirement.getFsn()+requirement.getWarehouse()+(requirement.getCreatedAt().toString());
        return requirementId;
    }

}
