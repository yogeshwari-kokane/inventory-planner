package fk.retail.ip.requirement.service;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.config.EmailConfiguration;
import fk.retail.ip.requirement.internal.command.*;
import fk.retail.ip.requirement.internal.command.emailHelper.ApprovalEmailHelper;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalAction;
import fk.retail.ip.requirement.internal.factory.RequirementStateFactory;
import fk.retail.ip.requirement.internal.repository.RequirementApprovalTransitionRepository;
import fk.retail.ip.requirement.internal.repository.RequirementEventLogRepository;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalStateV2;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.states.RequirementState;
import fk.retail.ip.requirement.model.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;

import javax.ws.rs.core.StreamingOutput;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by yogeshwari.k on 11/05/17.
 */
@Slf4j
public class RequirementServiceV2 {

    private final RequirementRepository requirementRepository;
    private final SearchFilterCommandV2 searchFilterCommand;
    private final Provider<SearchCommandV2> searchCommandProvider;
    private final RequirementStateFactory requirementStateFactory;
    private final ApprovalService approvalService;
    private final RequirementApprovalTransitionRepository requirementApprovalStateTransitionRepository;
    private final FdpRequirementIngestorImpl fdpRequirementIngestor;
    private final RequirementEventLogRepository requirementEventLogRepository;
    private final ApprovalEmailHelper appovalEmailHelper;
    private final EmailConfiguration emailConfiguration;

    @Inject
    public RequirementServiceV2(RequirementRepository requirementRepository,
                              Provider<SearchCommandV2> searchCommandProvider,
                              SearchFilterCommandV2 searchFilterCommand,
                              RequirementStateFactory requirementStateFactory,
                              ApprovalService approvalService,
                                RequirementApprovalTransitionRepository requirementApprovalStateTransitionRepository,
                                FdpRequirementIngestorImpl fdpRequirementIngestor,
                                RequirementEventLogRepository requirementEventLogRepository,
                                ApprovalEmailHelper appovalEmailHelper,
                                EmailConfiguration emailConfiguration) {

        this.requirementRepository = requirementRepository;
        this.searchFilterCommand = searchFilterCommand;
        this.searchCommandProvider = searchCommandProvider;
        this.requirementStateFactory = requirementStateFactory;
        this.approvalService = approvalService;
        this.requirementApprovalStateTransitionRepository = requirementApprovalStateTransitionRepository;
        this.fdpRequirementIngestor = fdpRequirementIngestor;
        this.requirementEventLogRepository = requirementEventLogRepository;
        this.appovalEmailHelper = appovalEmailHelper;
        this.emailConfiguration = emailConfiguration;
    }

    public SearchResponseV2.GroupedResponse searchV2(RequirementSearchRequestV2 request) throws JSONException {
        log.info("Search Requirement request received " + request);
        int pageNo = request.getPage();
        int pageSize = request.getPageSize();
        String requirementState = (String) request.getFilters().get("state");
        //TODO: remove this
        List<String> states = RequirementApprovalStateV2.getOldState(requirementState);
        String group = (String) request.getFilters().get("group");
        List<String> fsns = searchFilterCommand.getSearchFilterFsns(request.getFilters());
        if(fsns == null || fsns.isEmpty()) return new SearchResponseV2.GroupedResponse(0, pageNo, pageSize);
        List <String> stateFsns = requirementRepository.findFsnsByStateFsns(states, fsns, pageNo, pageSize);
        if(stateFsns == null || stateFsns.isEmpty()) return new SearchResponseV2.GroupedResponse(0, pageNo, pageSize);
        Long totalFsns = requirementRepository.findStateFsnsCount(states, fsns);
        List<Requirement> requirements = requirementRepository.findCurrentRequirementsByStateFsns(states, stateFsns);
        log.info("Search Request for {} number of requirements", requirements.size());
        Map<String, SearchResponseV2> fsnToSearchItemsMap =  searchCommandProvider.get().execute(requirements, requirementState, group);
        List<SearchResponseV2> searchResponses = fsnToSearchItemsMap.entrySet().stream().map(s -> s.getValue()).collect(Collectors.toList());
        SearchResponseV2.GroupedResponse groupedResponse = new SearchResponseV2.GroupedResponse(totalFsns, pageNo, pageSize);
        groupedResponse.setGroupedRequirements(searchResponses);
        return groupedResponse;
    }

    public StreamingOutput downloadRequirement(DownloadRequirementRequest2 downloadRequirementRequest) {
        List<String> fsns;
        Map<String, Object> filters = downloadRequirementRequest.getFilters();
        String requirementState = filters.get("state").toString();
        //TODO: remove this
        List<String> states = RequirementApprovalStateV2.getOldState(requirementState);
        boolean all = downloadRequirementRequest.isAll();
        boolean isLastAppSupplierRequired = downloadRequirementRequest.isLastAppSupplierRequired();
        if (all) {
            fsns = searchFilterCommand.getSearchFilterFsns(filters);
        }
        else {
            fsns = downloadRequirementRequest.getFsns();
        }
        List<Requirement> requirements = requirementRepository.findCurrentRequirementsByStateFsns(states, fsns);
        requirements = requirements.stream().filter(requirement -> !requirement.getWarehouse().equals("all")).collect(Collectors.toList());
        RequirementState state = requirementStateFactory.getRequirementState(states.get(0));
        return state.download(requirements, isLastAppSupplierRequired);
    }

    public String changeState(RequirementApprovalRequestV2 request, String userId) throws JSONException {
        log.info("Approval request received for " + request);
        RequirementApprovalAction action = RequirementApprovalAction.valueOf(request.getFilters().get("projection_action").toString());
        boolean forward = action.isForward();
        List<String> fsns = request.getFsns();
        String state = (String) request.getFilters().get("state");
        Function<Requirement, String> getter = Requirement::getState;
        List<Requirement> requirements;
        String groupName = request.getFilters().containsKey("group") ? (request.getFilters().get("group")).toString() : "";
        List<String> filteredFsns = searchFilterCommand.getSearchFilterFsns(request.getFilters());
        getFsnsIntersection(filteredFsns, fsns);
        requirements = requirementRepository.findCurrentRequirementsByStateFsns(state, filteredFsns);
        log.info("Change state Request for {} number of requirements", requirements.size());
        approvalService.changeState(
                requirements,
                state,
                userId,
                forward,
                getter,
                groupName,
                new ApprovalService.CopyOnStateChangeAction(requirementRepository,
                        requirementApprovalStateTransitionRepository,
                        fdpRequirementIngestor,
                        requirementEventLogRepository,
                        appovalEmailHelper,
                        emailConfiguration)
        );
        log.info("State changed for {} number of requirements", requirements.size());
        return "{\"msg\":\"Moved " + requirements.size() + " requirements to new state.\"}";
    }


    private void getFsnsIntersection(List<String> fsns, List<String> otherFsns) {
        if (otherFsns != null && !otherFsns.isEmpty()) {
            fsns.retainAll(otherFsns);
        }
    }

}
