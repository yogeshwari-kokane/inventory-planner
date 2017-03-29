package fk.retail.ip.fdp.internal.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
public class FdpClientIngestor {
    private final RestbusMessageSender restbusMessageSender;
    private final ObjectMapper mapper;
    private final FdpConfiguration fdpConfiguration;

    @Inject
    public FdpClientIngestor(RestbusMessageSender restbusMessageSender, ObjectMapper mapper, FdpConfiguration fdpConfiguration){
        this.restbusMessageSender = restbusMessageSender;
        this.mapper = mapper;
        this.fdpConfiguration = fdpConfiguration;
    }

    public void pushToFdp(FdpPayload fdpPayload) {
        Message message = getMessageInstance();
        try {
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            message.setPayload(mapper.writeValueAsString(fdpPayload));
            restbusMessageSender.send(message);
        } catch (JsonProcessingException e) {
            log.error("Unable to serialize request object ", e);
        }
    }

    private Message getMessageInstance() {
        Message message = new Message();
        message.setExchangeName(fdpConfiguration.getQueueName());
        message.setExchangeType("queue");
        message.setHttpMethod("POST");
        message.setHttpUri(fdpConfiguration.getUrl());
        message.setAppId("fk-ip-inventory-planner");
        return message;
    }
}
