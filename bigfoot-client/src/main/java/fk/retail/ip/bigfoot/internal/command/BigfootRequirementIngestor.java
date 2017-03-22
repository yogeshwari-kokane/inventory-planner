package fk.retail.ip.bigfoot.internal.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.restbus.client.entity.Message;
import com.google.inject.Inject;
import fk.retail.ip.bigfoot.config.BigfootConfiguration;
import fk.retail.ip.bigfoot.model.*;
import fk.sp.common.restbus.sender.RestbusMessageSender;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Slf4j
public class BigfootRequirementIngestor {
    private final RestbusMessageSender restbusMessageSender;
    private final ObjectMapper mapper;
    private final BigfootConfiguration bigfootConfiguration;

    @Inject
    public BigfootRequirementIngestor(RestbusMessageSender restbusMessageSender, ObjectMapper mapper, BigfootConfiguration bigfootConfiguration){
        this.restbusMessageSender = restbusMessageSender;
        this.mapper = mapper;
        this.bigfootConfiguration = bigfootConfiguration;
    }

    public void pushToBigfoot(BatchBigfootRequirementEventEntityPayload batchBigfootRequirementEventEntityPayload) {
        Message message = getMessageInstance();
        try {
            message.setPayload(mapper.writeValueAsString(batchBigfootRequirementEventEntityPayload));
            restbusMessageSender.send(message);
        } catch (JsonProcessingException e) {
            log.error("Unable to serialize request object ", e);
        }
    }

    private Message getMessageInstance() {
        Message message = new Message();
        message.setExchangeName(bigfootConfiguration.getRequirementQueueName());
        message.setExchangeType("queue");
        message.setHttpMethod("POST");
        message.setHttpUri(bigfootConfiguration.getUrl());
        message.setReplyTo(bigfootConfiguration.getRequirementQueueName());
        message.setReplyToHttpMethod("POST");
        message.setAppId("fk-ip-inventory-planner");
        return message;
    }
}
