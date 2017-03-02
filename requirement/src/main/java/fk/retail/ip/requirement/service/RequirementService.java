package fk.retail.ip.requirement.service;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.DownloadRequirementRequest;
import fk.retail.ip.requirement.model.RequirementApprovalRequest;
import fk.retail.ip.requirement.model.RequirementManager;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.ws.rs.core.StreamingOutput;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;

import static java.util.stream.Collectors.toSet;

/**
 * @author nidhigupta.m
 * @author Pragalathan M <pragalathan.m@flipkart.com>
 */
@Slf4j
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final RequirementManager requirementManager;
    private final ApprovalService approvalService;

    @Inject
    public RequirementService(RequirementRepository requirementRepository, RequirementManager requirementManager, ApprovalService approvalService) {
        this.requirementRepository = requirementRepository;
        this.requirementManager = requirementManager;
        this.approvalService = approvalService;

    }

    public StreamingOutput downloadRequirement(DownloadRequirementRequest downloadRequirementRequest) {
        List<Long> requirementIds = downloadRequirementRequest.getRequirementIds();
        String requirementState = downloadRequirementRequest.getState();
        boolean isLastAppSupplierRequired = downloadRequirementRequest.isLastAppSupplierRequired();
        List<Requirement> requirements;
        if (!requirementIds.isEmpty()) {
            requirements = requirementRepository.find(requirementIds);
        } else {
            requirements = requirementRepository.find(requirementState);
        }

        StreamingOutput output = requirementManager.withRequirements(requirements).download(requirementState, isLastAppSupplierRequired);
        return output;
    }

    public String changeState(RequirementApprovalRequest request) throws JSONException {
        String action = request.getFilters().get("projection_action").toString();
        Function<Requirement, String> getter = Requirement::getState;
        List<Requirement> requirements = requirementRepository.findRequirements((List<Long>) request.getFilters().get("id"), (String) request.getFilters().get("state"), request.getFilters());
        Set<Long> projectionIds = requirements.stream().map(Requirement::getProjectionId).collect(toSet());

        approvalService.changeState(requirements, "userId", action, getter, new ApprovalService.CopyOnStateChangeAction(requirementRepository));
        requirementRepository.updateProjection(projectionIds, approvalService.getTargetState(action));
        return "{\"msg\":\"Moved " + requirements.size() + " projections to new state.\"}";
    }
}
