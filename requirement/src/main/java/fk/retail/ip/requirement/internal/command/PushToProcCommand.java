package fk.retail.ip.requirement.internal.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.restbus.client.entity.Message;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.RequirementConfiguration;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.model.CreatePushToProcRequest;
import fk.retail.ip.requirement.model.PushToProcRequest;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yogeshwari.k on 07/04/17.
 */
@Slf4j
public class PushToProcCommand {

    private final ObjectMapper mapper;
    private final RequirementConfiguration requirementConfiguration;

    @Inject
    PushToProcCommand(ObjectMapper mapper, RequirementConfiguration requirementConfiguration) {
        this.mapper = mapper;
        this.requirementConfiguration = requirementConfiguration;
    }

    private Map<Long, PushToProcRequest> getPushToProcRequest(List<Requirement> requirements) {
        return requirements.stream()
                .collect(Collectors.toMap(requirement -> requirement.getId(), requirement -> {
                    return PushToProcRequest.builder()
                            .fsn(requirement.getFsn())
                            .sku("SKU0000000000000")
                            .quantity((int)requirement.getQuantity())
                            .local(true)
                            .supplierApp((float)requirement.getApp())
                            .supplierMrp((float)requirement.getMrp())
                            .sourceId(requirement.getSupplier())
                            .requiredByDate(getRequiredByDate(requirement))
                            .requirementType(requirement.getProcType())
                            .currency(requirement.getCurrency())
                            .warehouseId(requirement.getWarehouse())
                            .build();
                }));
    }

    private Date getRequiredByDate(Requirement requirement) {
        // set required by date based on holiday calendar
        DateTime dt = new DateTime().plusDays(requirement.getSla());
        DateTime adjustedDate = dt;
        if (dt.getDayOfWeek() == DateTimeConstants.SATURDAY) {
            adjustedDate = dt.plusDays(2);
        }
        if (dt.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            adjustedDate = dt.plusDays(1);
        }
        return adjustedDate.toDate();
    }

    private Message getMessageInstance() {
        Message message = new Message();
        message.setExchangeName(requirementConfiguration.getRequirementQueueName());
        message.setExchangeType("queue");
        message.setHttpMethod("POST");
        message.setHttpUri(requirementConfiguration.getUrl());
        message.setReplyTo(requirementConfiguration.getRequirementQueueName());
        message.setReplyToHttpMethod("POST");
        message.setAppId("fk-rp-populator");
        return message;
    }

    public void pushToProc(List<Requirement> requirements) {
        Map<Long, PushToProcRequest> allRequirements = getPushToProcRequest(requirements);
        allRequirements.forEach((id, requirement) -> {
            Message message = getMessageInstance();
            CreatePushToProcRequest pushToProcRequest = new CreatePushToProcRequest();
            pushToProcRequest.getPushToProcRequestList().add(requirement);
            try {
                message.setPayload(mapper.writeValueAsString(pushToProcRequest));
                message.setReplyToHttpUri(requirementConfiguration.getCallbackUrl() + id);
                //restbusMessageSender.send(message);
            } catch (JsonProcessingException e) {
                log.error("Unable to serialize request object ", e);
            }
        });
    }

}
