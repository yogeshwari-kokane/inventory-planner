package fk.retail.ip.fdp.internal.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.restbus.client.entity.Message;
import com.google.inject.Inject;
import fk.retail.ip.fdp.config.FdpConfiguration;
import fk.retail.ip.fdp.model.*;
import fk.sp.common.restbus.sender.RestbusMessageSender;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Slf4j
public class FdpRequirementIngestor {
    private final RestbusMessageSender restbusMessageSender;
    private final ObjectMapper mapper;
    private final FdpConfiguration fdpConfiguration;

    @Inject
    public FdpRequirementIngestor(RestbusMessageSender restbusMessageSender, ObjectMapper mapper, FdpConfiguration fdpConfiguration){
        this.restbusMessageSender = restbusMessageSender;
        this.mapper = mapper;
        this.fdpConfiguration = fdpConfiguration;
    }

    public void pushToFdp(BatchFdpRequirementEventEntityPayload batchFdpRequirementEventEntityPayload) {
        Message message = getMessageInstance();
        try {
            message.setPayload(mapper.writeValueAsString(batchFdpRequirementEventEntityPayload));
            restbusMessageSender.send(message);
        } catch (JsonProcessingException e) {
            log.error("Unable to serialize request object ", e);
        }
    }

    private Message getMessageInstance() {
        Message message = new Message();
        message.setExchangeName(fdpConfiguration.getRequirementQueueName());
        message.setExchangeType("queue");
        message.setHttpMethod("POST");
        message.setHttpUri(fdpConfiguration.getUrl());
        message.setReplyTo(fdpConfiguration.getRequirementQueueName());
        message.setReplyToHttpMethod("POST");
        message.setAppId("fk-ip-inventory-planner");
        return message;
    }
}
