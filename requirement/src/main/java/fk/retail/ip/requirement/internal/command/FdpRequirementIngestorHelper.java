package fk.retail.ip.requirement.internal.command;

import fk.retail.ip.fdp.model.*;
import fk.retail.ip.requirement.model.*;
import fk.retail.ip.requirement.internal.entities.Requirement;

import java.util.List;
import fk.retail.ip.fdp.internal.command.FdpRequirementIngestor;
/**
 * Created by yogeshwari.k on 22/03/17.
 */
public class FdpRequirementIngestorHelper extends FdpIngestorHelper<List<RequirementChangeRequest>> {

    BatchFdpEventEntityPayload<FdpRequirementEntityData,FdpRequirementEventData> batchFdpRequirementEventEntityPayload = new BatchFdpEventEntityPayload();
    FdpEntityMapper createRequirementEntityPayload = new CreateRequirementEntityPayload();
    FdpEventMapper createRequirementEventPayload = new CreateRequirementEventPayload();
    FdpRequirementIngestor fdpRequirementIngestor;

    @Override
    public BatchFdpEventEntityPayload pushToFdp(List<RequirementChangeRequest> requirementChangeRequests){
        requirementChangeRequests.forEach(req -> {
            String requirementId= getRequirementId(req.getRequirement());
            FdpEntityPayload<FdpRequirementEntityData> fdpRequirementEntityPayload = createRequirementEntityPayload.convertRequirementToEntityPayload(requirementId,req.getRequirement());
            List<FdpEventPayload<FdpRequirementEventData>> fdpRequirementEventPayload = createRequirementEventPayload.convertRequirementToEventPayload(requirementId,req.getChangeMaps());
            batchFdpRequirementEventEntityPayload.getRequirementEntity().add(fdpRequirementEntityPayload);
            batchFdpRequirementEventEntityPayload.getRequirementEvent().addAll(fdpRequirementEventPayload);
            //fdpRequirementIngestor.pushToFdp(batchFdpRequirementEventEntityPayload);
        });

        //TODO: remove return (used only for testing payload creation)
        return batchFdpRequirementEventEntityPayload;
    }

    private String getRequirementId(Requirement requirement) {
        String requirementId = requirement.getFsn()+requirement.getWarehouse()+(requirement.getCreatedAt().toString());
        return requirementId;
    }

}
