package fk.retail.ip.requirement.service;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.command.*;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.factory.RequirementStateFactory;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.states.RequirementState;
import fk.retail.ip.requirement.model.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;

import javax.ws.rs.core.StreamingOutput;
import java.util.*;
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
    private final int defaultPageSize = 20;
    private final int defaultPageNo = 1;

    @Inject
    public RequirementServiceV2(RequirementRepository requirementRepository,
                              Provider<SearchCommandV2> searchCommandProvider,
                              SearchFilterCommandV2 searchFilterCommand,
                                RequirementStateFactory requirementStateFactory) {

        this.requirementRepository = requirementRepository;
        this.searchFilterCommand = searchFilterCommand;
        this.searchCommandProvider = searchCommandProvider;
        this.requirementStateFactory = requirementStateFactory;
    }

    public SearchResponseV2.GroupedResponse searchV2(RequirementSearchRequestV2 request) throws JSONException {
        log.info("Search Requirement request received " + request);
        Integer pageNo = request.getFilters().get("page")!=null ?
                Integer.parseInt(request.getFilters().get("page").toString()): defaultPageNo;
        Integer pageSize = request.getFilters().get("page_size")!=null ?
                Integer.parseInt(request.getFilters().get("page_size").toString()):defaultPageSize;
        String state = (String) request.getFilters().get("state");
        String group = (String) request.getFilters().get("group");
        List<String> fsns = searchFilterCommand.getSearchFilterFsns(request.getFilters());
        if(fsns == null || fsns.isEmpty()) return new SearchResponseV2.GroupedResponse(0, pageNo, pageSize);
        List <String> stateFsns = requirementRepository.findFsnsByStateFsns(state, fsns, pageNo, pageSize);
        if(stateFsns == null || stateFsns.isEmpty()) return new SearchResponseV2.GroupedResponse(0, pageNo, pageSize);
        Long totalFsns = requirementRepository.findStateFsnsCount(state, fsns);
        List<Requirement> requirements = requirementRepository.findCurrentRequirementsByStateFsns(state, stateFsns);
        log.info("Search Request for {} number of requirements", requirements.size());
        Map<String, SearchResponseV2> fsnToSearchItemsMap =  searchCommandProvider.get().execute(requirements, state, group);
        List<SearchResponseV2> searchResponses = fsnToSearchItemsMap.entrySet().stream().map(s -> s.getValue()).collect(Collectors.toList());
        SearchResponseV2.GroupedResponse groupedResponse = new SearchResponseV2.GroupedResponse(totalFsns, pageNo, pageSize);
        groupedResponse.setGroupedRequirements(searchResponses);
        return groupedResponse;
    }

    public StreamingOutput downloadRequirement(DownloadRequirementRequest2 downloadRequirementRequest) {
        List<String> fsns = downloadRequirementRequest.getFsns();
        Map<String, Object> filters = downloadRequirementRequest.getFilters();
        String requirementState = filters.get("state").toString();
        boolean isLastAppSupplierRequired = downloadRequirementRequest.isLastAppSupplierRequired();
        List<String> filteredFsns = searchFilterCommand.getSearchFilterFsns(filters);
        getFsnsIntersection(filteredFsns, fsns);
        List<Requirement> requirements = requirementRepository.findCurrentRequirementsByStateFsns(requirementState, filteredFsns);
        requirements = requirements.stream().filter(requirement -> !requirement.getWarehouse().equals("all")).collect(Collectors.toList());
        RequirementState state = requirementStateFactory.getRequirementState(requirementState);
        return state.download(requirements, isLastAppSupplierRequired);
    }

    private void getFsnsIntersection(List<String> fsns, List<String> otherFsns) {
        if (otherFsns != null && !otherFsns.isEmpty()) {
            fsns.retainAll(otherFsns);
        }
    }

}
