package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.fdp.model.*;
import fk.retail.ip.requirement.model.*;
import fk.retail.ip.requirement.internal.entities.Requirement;

import java.util.List;

/**
 * Created by yogeshwari.k on 22/03/17.
 */
public class FdpRequirementIngestorImpl implements FdpIngestor<List<RequirementChangeRequest>> {

    private final FdpEntityMapper requirementToFdpEntityMapper;
    private final FdpEventMapper requirementToFdpEventMapper;

    fk.retail.ip.fdp.internal.command.FdpIngestor fdpIngestor;

    @Inject
    FdpRequirementIngestorImpl(FdpEntityMapper requirementToFdpEntityMapper, FdpEventMapper requirementToFdpEventMapper) {
        this.requirementToFdpEntityMapper = requirementToFdpEntityMapper;
        this.requirementToFdpEventMapper = requirementToFdpEventMapper;
    }

    @Override
    public BatchFdpEventEntityPayload pushToFdp(List<RequirementChangeRequest> requirementChangeRequests){
        BatchFdpEventEntityPayload<FdpRequirementEntityData,FdpRequirementEventData> batchFdpRequirementEventEntityPayload = new BatchFdpEventEntityPayload();
        requirementChangeRequests.forEach(req -> {
            String requirementId= getRequirementId(req.getRequirement());
            FdpEntityPayload<FdpRequirementEntityData> fdpRequirementEntityPayload = requirementToFdpEntityMapper.convertRequirementToEntityPayload(requirementId,req.getRequirement());
            List<FdpEventPayload<FdpRequirementEventData>> fdpRequirementEventPayload = requirementToFdpEventMapper.convertRequirementToEventPayload(requirementId,req.getRequirementChangeMaps());
            batchFdpRequirementEventEntityPayload.getEntities().add(fdpRequirementEntityPayload);
            batchFdpRequirementEventEntityPayload.getEvents().addAll(fdpRequirementEventPayload);
            //fdpIngestor.pushToFdp(batchFdpRequirementEventEntityPayload);
        });

        //TODO: remove return (used only for testing payload creation)
        return batchFdpRequirementEventEntityPayload;
    }

    private String getRequirementId(Requirement requirement) {
        String requirementId = requirement.getFsn()+requirement.getWarehouse()+(requirement.getCreatedAt().toString());
        return requirementId;
    }

}
