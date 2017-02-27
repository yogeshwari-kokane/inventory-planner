package fk.retail.ip.requirement.service;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.DownloadRequirementRequest;
import fk.retail.ip.requirement.model.RequirementApprovalRequest;
import fk.retail.ip.requirement.model.RequirementManager;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.ws.rs.core.StreamingOutput;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;

import static java.util.stream.Collectors.toList;

/**
 * Created by nidhigupta.m on 26/01/17.
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
        //        log.info("params {}", request);
        String action = request.getProjection().getAction();

        Function<Requirement, String> getter = Requirement::getState;
        List<Requirement> requirements = request.isAll()
                ? requirementRepository.find(request.getState())
                : requirementRepository.find(Arrays.asList(request.getIds()).stream().map(Integer::longValue).collect(toList()));
        approvalService.changeState(requirements, "userId", action, getter, new ApprovalService.CopyOnStateChangeAction(requirementRepository));
        return "{\"msg\":\"Moved " + requirements.size() + " projections to new state.\"}";
    }
}
