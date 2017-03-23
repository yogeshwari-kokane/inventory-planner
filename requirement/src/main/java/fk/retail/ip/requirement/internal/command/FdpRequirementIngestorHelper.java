package fk.retail.ip.requirement.internal.command;

import fk.retail.ip.fdp.model.*;
import fk.retail.ip.requirement.model.*;
import fk.retail.ip.requirement.internal.entities.Requirement;

import java.util.List;
import fk.retail.ip.fdp.internal.command.FdpRequirementIngestor;
/**
 * Created by yogeshwari.k on 22/03/17.
 */
public class FdpRequirementIngestorHelper {
    BatchFdpRequirementEventEntityPayload batchFdpRequirementEventEntityPayload = new BatchFdpRequirementEventEntityPayload();
    RequirementEntityMapper requirementEntityMapper;
    RequirementEventMapper requirementEventMapper;
    FdpRequirementIngestor fdpRequirementIngestor;
    CreateRequirementEntityPayload createRequirementEntityPayload = new CreateRequirementEntityPayload();
    CreateRequirementEventPayload createRequirementEventPayload = new CreateRequirementEventPayload();

    public BatchFdpRequirementEventEntityPayload pushToFdp(List<RequirementChangeRequest> requirementChangeRequests){
        requirementChangeRequests.forEach(req -> {
            String requirementId= getRequirementId(req.getRequirement());
            FdpRequirementEntityPayload fdpRequirementEntityPayload = createRequirementEntityPayload.convertRequirementToEntityPayload(requirementId,req.getRequirement());
            List<FdpRequirementEventPayload> fdpRequirementEventPayload = createRequirementEventPayload.convertRequirementToEventPayload(requirementId,req.getChangeMaps());
            batchFdpRequirementEventEntityPayload.getRequirementEntity().add(fdpRequirementEntityPayload);
            batchFdpRequirementEventEntityPayload.getRequirementEvent().addAll(fdpRequirementEventPayload);
            fdpRequirementIngestor.pushToFdp(batchFdpRequirementEventEntityPayload);
        });
        return batchFdpRequirementEventEntityPayload;
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
