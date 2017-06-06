package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.fdp.internal.command.FdpClientIngestor;
import fk.retail.ip.fdp.model.*;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.model.*;
import fk.retail.ip.requirement.internal.entities.Requirement;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by yogeshwari.k on 22/03/17.
 */
@Slf4j
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
        for(List<RequirementChangeRequest> requestList : Lists.partition(requirementChangeRequests, 100)) {
            batchFdpRequirementEventEntityPayload.getPurchaseRequirementEntity().clear();
            batchFdpRequirementEventEntityPayload.getPurchaseRequirementEvent().clear();
            requestList.forEach(req -> {
                if(!req.getRequirement().getState().equals(RequirementApprovalState.ERROR.toString())) {
                    FdpEntityPayload<FdpRequirementEntityData> fdpRequirementEntityPayload = requirementToFdpEntityMapper.
                            convertToEntityPayload(req.getRequirement());
                    List<FdpEventPayload<FdpRequirementEventData>> fdpRequirementEventPayload = requirementToFdpEventMapper.
                            convertToEventPayload(req.getRequirement().getRequirementId(), req.getRequirementChangeMaps());
                    batchFdpRequirementEventEntityPayload.getPurchaseRequirementEntity().add(fdpRequirementEntityPayload);
                    batchFdpRequirementEventEntityPayload.getPurchaseRequirementEvent().addAll(fdpRequirementEventPayload);
                }
            });

            log.info("Pushing {} number of requirement entities to fdp", batchFdpRequirementEventEntityPayload.getPurchaseRequirementEntity().size());
            log.info("Pushing {} number of requirement events to fdp", batchFdpRequirementEventEntityPayload.getPurchaseRequirementEvent().size());
            if (!batchFdpRequirementEventEntityPayload.getPurchaseRequirementEntity().isEmpty())
                fdpClientIngestor.pushToFdp(batchFdpRequirementEventEntityPayload);
        }
    }

}
