package fk.retail.ip.requirement.service;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.config.EmailConfiguration;
import fk.retail.ip.requirement.internal.command.*;
import fk.retail.ip.requirement.internal.command.emailHelper.ApprovalEmailHelper;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.factory.RequirementStateFactoryV2;
import fk.retail.ip.requirement.internal.repository.RequirementApprovalTransitionRepositoryV2;
import fk.retail.ip.requirement.internal.repository.RequirementEventLogRepository;
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
    private final RequirementStateFactoryV2 requirementStateFactory;
    private final ApprovalServiceV2 approvalService;
    private final RequirementApprovalTransitionRepositoryV2 requirementApprovalStateTransitionRepository;
    private final FdpRequirementIngestorImpl fdpRequirementIngestor;
    private final RequirementEventLogRepository requirementEventLogRepository;
    private final ApprovalEmailHelper appovalEmailHelper;
    private final EmailConfiguration emailConfiguration;
    private final PushToProcCommandV2 pushToProcCommand;

    @Inject
    public RequirementServiceV2(RequirementRepository requirementRepository,
                              Provider<SearchCommandV2> searchCommandProvider,
                              SearchFilterCommandV2 searchFilterCommand,
                              RequirementStateFactoryV2 requirementStateFactory,
                              ApprovalServiceV2 approvalService,
                                RequirementApprovalTransitionRepositoryV2 requirementApprovalStateTransitionRepository,
                                FdpRequirementIngestorImpl fdpRequirementIngestor,
                                RequirementEventLogRepository requirementEventLogRepository,
                                ApprovalEmailHelper appovalEmailHelper,
                                EmailConfiguration emailConfiguration,
                                PushToProcCommandV2 pushToProcCommand) {

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
        this.pushToProcCommand = pushToProcCommand;
    }

    public SearchResponseV2.GroupedResponse searchV2(RequirementSearchRequestV2 request) throws JSONException {
        log.info("Search Requirement request received " + request);
        int pageNo = request.getPage();
        int pageSize = request.getPageSize();
        String requirementState = (String) request.getFilters().get("state");
        String group = (String) request.getFilters().get("group");
        List<String> fsns = searchFilterCommand.getSearchFilterFsns(request.getFilters());
        if(fsns == null || fsns.isEmpty()) return new SearchResponseV2.GroupedResponse(0, pageNo, pageSize);
        List <String> stateFsns = requirementRepository.findFsnsByStateFsns(requirementState, fsns, pageNo, pageSize);
        if(stateFsns == null || stateFsns.isEmpty()) return new SearchResponseV2.GroupedResponse(0, pageNo, pageSize);
        Long totalFsns = requirementRepository.findStateFsnsCount(requirementState, fsns);
        List<Requirement> requirements = requirementRepository.findCurrentRequirementsByStateFsns(requirementState, stateFsns);
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
        boolean all = downloadRequirementRequest.isAll();
        boolean isLastAppSupplierRequired = downloadRequirementRequest.isLastAppSupplierRequired();
        if (all) {
            fsns = searchFilterCommand.getSearchFilterFsns(filters);
        }
        else {
            fsns = downloadRequirementRequest.getFsns();
        }
        List<Requirement> requirements = requirementRepository.findCurrentRequirementsByStateFsns(requirementState, fsns);
        requirements = requirements.stream().filter(requirement -> !requirement.getWarehouse().equals("all")).collect(Collectors.toList());
        RequirementState state = requirementStateFactory.getRequirementState(requirementState);
        return state.download(requirements, isLastAppSupplierRequired);
    }

    public String changeState(RequirementApprovalRequestV2 request, String userId) throws JSONException {
        log.info("Approval request received for " + request);
        List<String> fsns;
        Map<String, Object> filters = request.getFilters();
        boolean forward = request.isForward();
        String state = (String) request.getFilters().get("state");
        boolean all = request.isAll();
        if (all) {
            fsns = searchFilterCommand.getSearchFilterFsns(filters);
        }
        else {
            fsns = request.getFsns();
        }
        Function<Requirement, String> getter = Requirement::getState;
        List<Requirement> requirements;
        String groupName = request.getFilters().containsKey("group") ? (filters.get("group")).toString() : "";
        requirements = requirementRepository.findCurrentRequirementsByStateFsns(state, fsns);
        log.info("Change state Request for {} number of requirements", requirements.size());
        approvalService.changeState(
                requirements,
                state,
                userId,
                forward,
                getter,
                groupName,
                new ApprovalServiceV2.CopyOnStateChangeAction(requirementRepository,
                        requirementApprovalStateTransitionRepository,
                        fdpRequirementIngestor,
                        requirementEventLogRepository,
                        appovalEmailHelper,
                        emailConfiguration)
        );
        log.info("State changed for {} number of requirements", requirements.size());
        return "{\"msg\":\"Moved " + requirements.size() + " requirements to new state.\"}";
    }

    public String pushToProc(RaisePORequestV2 request, String userId) throws JSONException {
        log.info("Push to proc request received " + request);
        Map<String, Object> filters = request.getFilters();
        String state = (String) request.getFilters().get("state");
        List<String> fsns;
        boolean all = request.isAll();
        if (all) {
            fsns = searchFilterCommand.getSearchFilterFsns(filters);
        }
        else {
            fsns = request.getFsns();
        }
        List<Requirement> requirements = requirementRepository.findCurrentRequirementsByStateFsns(state, fsns);
        int pushedRequirements = pushToProcCommand.pushToProc(requirements,userId);
        log.info("Moved {} number of requirements to Procurement", requirements.size());
        return "{\"msg\":\"Moved " + pushedRequirements +" requirements to Procurement.\"}";
    }

}
