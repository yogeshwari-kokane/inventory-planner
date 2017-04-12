package fk.retail.ip.requirement.internal.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.restbus.client.entity.Message;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.RequirementConfiguration;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementApprovalTransition;
import fk.retail.ip.requirement.internal.enums.FdpRequirementEventType;
import fk.retail.ip.requirement.internal.enums.OverrideKey;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.RequirementApprovalTransitionRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.CreatePushToProcRequest;
import fk.retail.ip.requirement.model.PushToProcRequest;
import fk.retail.ip.requirement.model.RequirementChangeMap;
import fk.retail.ip.requirement.model.RequirementChangeRequest;
import fk.sp.common.restbus.sender.RestbusMessageSender;
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
    private final RestbusMessageSender restbusMessageSender;
    private final RequirementRepository requirementRepository;
    private final RequirementApprovalTransitionRepository requirementApprovalStateTransitionRepository;

    @Inject
    PushToProcCommand(ObjectMapper mapper, RequirementConfiguration requirementConfiguration, RestbusMessageSender restbusMessageSender, RequirementRepository requirementRepository, RequirementApprovalTransitionRepository requirementApprovalStateTransitionRepository) {
        this.mapper = mapper;
        this.requirementConfiguration = requirementConfiguration;
        this.restbusMessageSender = restbusMessageSender;
        this.requirementRepository = requirementRepository;
        this.requirementApprovalStateTransitionRepository = requirementApprovalStateTransitionRepository;
    }

    private Map<Long, PushToProcRequest> getPushToProcRequest(List<Requirement> requirements) {
        return requirements.stream()
                .collect(Collectors.toMap(requirement -> requirement.getId(), requirement -> PushToProcRequest.builder()
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
                        .build()));
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
        message.setAppId("fk-ip-inventory-planner");
        return message;
    }

    private List<Requirement> createPushToProcRequirement(List<Requirement> requirements, String userId) {
        Map<Long, String> groupToTargetState = getGroupToTargetStateMap(RequirementApprovalState.IPC_FINALISED.toString(), true);
        log.info("Constructed map for group to Target state " + groupToTargetState);
        List<Requirement> pushToProcRequirements = Lists.newArrayList();
        requirements.forEach(requirement -> {
            Requirement newEntity = new Requirement(requirement);
            requirement.setCurrent(false);
            newEntity.setState(RequirementApprovalState.PUSHED_TO_PROC.toString());
            newEntity.setCreatedBy(userId);
            newEntity.setPreviousStateId(requirement.getId());
            newEntity.setCurrent(true);
            requirementRepository.persist(newEntity);
            pushToProcRequirements.add(newEntity);
        });
        log.info("Updating Projections tables for Requirements");
        requirementRepository.updateProjections(requirements, groupToTargetState);
        return pushToProcRequirements;
    }

    public void pushToProc(List<Requirement> requirements, String userId) {
        List<Requirement> pushToProcRequirements = createPushToProcRequirement(requirements,userId);
        Map<Long, PushToProcRequest> allRequirements = getPushToProcRequest(pushToProcRequirements);
        allRequirements.forEach((id, requirement) -> {
            Message message = getMessageInstance();
            CreatePushToProcRequest pushToProcRequest = new CreatePushToProcRequest();
            pushToProcRequest.getPushToProcRequestList().add(requirement);
            try {
                message.setPayload(mapper.writeValueAsString(pushToProcRequest));
                message.setReplyToHttpUri(requirementConfiguration.getCallbackUrl() + id);
                restbusMessageSender.send(message);
            } catch (JsonProcessingException e) {
                log.error("Unable to serialize request object ", e);
            }
        });
    }

    private Map<Long, String> getGroupToTargetStateMap(String fromState, boolean forward) {
        List<RequirementApprovalTransition> transitionList = requirementApprovalStateTransitionRepository.getApprovalTransition(fromState, forward);
        return transitionList.stream().collect(Collectors.toMap(RequirementApprovalTransition::getGroupId, RequirementApprovalTransition::getToState));
    }

}
