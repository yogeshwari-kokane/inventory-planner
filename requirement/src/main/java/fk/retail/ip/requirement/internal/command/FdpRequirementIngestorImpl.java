package fk.retail.ip.requirement.internal.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;
import fk.retail.ip.fdp.internal.command.FdpClientIngestor;
import fk.retail.ip.fdp.model.*;
import fk.retail.ip.requirement.model.*;
import fk.retail.ip.requirement.internal.entities.Requirement;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by yogeshwari.k on 22/03/17.
 */
public class FdpRequirementIngestorImpl implements FdpIngestor<List<RequirementChangeRequest>> {

    private final FdpEntityMapper requirementToFdpEntityMapper;
    private final FdpEventMapper requirementToFdpEventMapper;

    FdpClientIngestor fdpClientIngestor;

    @Inject
    FdpRequirementIngestorImpl(FdpEntityMapper requirementToFdpEntityMapper, FdpEventMapper requirementToFdpEventMapper) {
        this.requirementToFdpEntityMapper = requirementToFdpEntityMapper;
        this.requirementToFdpEventMapper = requirementToFdpEventMapper;
    }

    @Override
    public BatchFdpEventEntityPayload pushToFdp(List<RequirementChangeRequest> requirementChangeRequests) {
        BatchFdpEventEntityPayload<FdpRequirementEntityData,FdpRequirementEventData> batchFdpRequirementEventEntityPayload = new BatchFdpEventEntityPayload();
        requirementChangeRequests.forEach(req -> {
            String requirementId= getRequirementId(req.getRequirement());
            FdpEntityPayload<FdpRequirementEntityData> fdpRequirementEntityPayload = requirementToFdpEntityMapper.convertToEntityPayload(requirementId,req.getRequirement());
            List<FdpEventPayload<FdpRequirementEventData>> fdpRequirementEventPayload = requirementToFdpEventMapper.convertToEventPayload(requirementId,req.getRequirementChangeMaps());
            batchFdpRequirementEventEntityPayload.getEntities().add(fdpRequirementEntityPayload);
            batchFdpRequirementEventEntityPayload.getEvents().addAll(fdpRequirementEventPayload);
            //fdpClientIngestor.pushToFdp(batchFdpRequirementEventEntityPayload);
        });

        //TODO: remove return (used only for testing payload creation)
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String result = null;
        try {
            result = mapper.writeValueAsString(batchFdpRequirementEventEntityPayload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("result:"+result);
        return batchFdpRequirementEventEntityPayload;
    }

    private String getRequirementId(Requirement requirement) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String requirementId = requirement.getFsn()+"_"+requirement.getWarehouse()+"_"+(sdf.format(requirement.getCreatedAt()).toString());
        return requirementId;
    }

}
