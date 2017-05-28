package fk.retail.ip.segmentation.job;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.restbus.client.entity.Message;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

import fk.retail.ip.core.Constants;
import fk.retail.ip.core.repository.GroupRepository;
import fk.retail.ip.core.entities.IPGroup;
import fk.retail.ip.segmentation.config.GroupSegmentationConfiguration;
import fk.retail.ip.segmentation.model.GroupSegmentationRequest;
import fk.sp.common.restbus.sender.RestbusMessageSender;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nidhigupta.m on 24/04/17.
 */

@Transactional
@Slf4j
public class GroupSegmentationJob implements Job {

    private final GroupRepository groupRepository;
    private final GroupSegmentationConfiguration groupSegmentationConfiguration;
    private final RestbusMessageSender restbusMessageSender;
    private final ObjectMapper mapper;

    @Inject
    public GroupSegmentationJob(GroupRepository groupRepository, GroupSegmentationConfiguration groupSegmentationConfiguration,
                                RestbusMessageSender restbusMessageSender, ObjectMapper objectMapper) {
        this.groupRepository = groupRepository;
        this.groupSegmentationConfiguration = groupSegmentationConfiguration;
        this.restbusMessageSender = restbusMessageSender;
        this.mapper = objectMapper;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<IPGroup> groups = groupRepository.getGroupsForSegmentation();
        log.info("Running segmentation job on {} groups " , groups.size());
        groups.forEach(group -> sendToRestBus(group));
    }

    private void sendToRestBus(IPGroup group) {
        GroupSegmentationRequest groupSegmentationRequest = new GroupSegmentationRequest(group.getId(), group.getRule());
        Message message = getMessageInstance();
        message.setGroupId(group.getId().toString());
        try {
            message.setPayload(mapper.writeValueAsString(groupSegmentationRequest));
            restbusMessageSender.send(message);
        } catch (JsonProcessingException e) {
            log.error("Unable to serialize request object ", e);
        }
    }


    private Message getMessageInstance() {
        String url = groupSegmentationConfiguration.getUrl();
        Message message = new Message();
        message.setExchangeName(groupSegmentationConfiguration.getQueueName());
        message.setExchangeType("queue");
        message.setHttpMethod("POST");
        message.setHttpUri(url);
        message.setAppId(Constants.APP_ID.toString());
        return message;
    }



}
