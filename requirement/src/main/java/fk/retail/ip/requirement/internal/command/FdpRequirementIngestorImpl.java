package fk.retail.ip.requirement.internal.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import fk.retail.ip.fdp.internal.command.FdpClientIngestor;
import fk.retail.ip.fdp.model.*;
import fk.retail.ip.requirement.model.*;
import fk.retail.ip.requirement.internal.entities.Requirement;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by yogeshwari.k on 22/03/17.
 */
public class FdpRequirementIngestorImpl {

    private final FdpClientIngestor fdpClientIngestor;
    private final RequirementToFdpEntityMapper requirementToFdpEntityMapper;
    private final RequirementToFdpEventMapper requirementToFdpEventMapper;

    @Inject
    FdpRequirementIngestorImpl(FdpClientIngestor fdpClientIngestor, RequirementToFdpEntityMapper requirementToFdpEntityMapper, RequirementToFdpEventMapper requirementToFdpEventMapper) {
        this.fdpClientIngestor = fdpClientIngestor;
        this.requirementToFdpEntityMapper = requirementToFdpEntityMapper;
        this.requirementToFdpEventMapper = requirementToFdpEventMapper;
    }

    public void pushToFdp(List<RequirementChangeRequest> requirementChangeRequests) {
        BatchFdpRequirementEventEntityPayload batchFdpRequirementEventEntityPayload = new BatchFdpRequirementEventEntityPayload();
        requirementChangeRequests.forEach(req -> {
            String requirementId= getRequirementId(req.getRequirement());
            FdpEntityPayload<FdpRequirementEntityData> fdpRequirementEntityPayload = requirementToFdpEntityMapper.convertToEntityPayload(requirementId,req.getRequirement());
            List<FdpEventPayload<FdpRequirementEventData>> fdpRequirementEventPayload = requirementToFdpEventMapper.convertToEventPayload(requirementId,req.getRequirementChangeMaps());
            batchFdpRequirementEventEntityPayload.getPurchaseRequirementEntity().add(fdpRequirementEntityPayload);
            batchFdpRequirementEventEntityPayload.getPurchaseRequirementEvent().addAll(fdpRequirementEventPayload);
        });

        if(!batchFdpRequirementEventEntityPayload.getPurchaseRequirementEntity().isEmpty())
            fdpClientIngestor.pushToFdp(batchFdpRequirementEventEntityPayload);
    }

    private String getRequirementId(Requirement requirement) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String requirementId = requirement.getFsn()+"_"+requirement.getWarehouse()+"_"+(sdf.format(requirement.getCreatedAt()).toString());
        return requirementId;
    }

}
