package fk.retail.ip.requirement.service;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.command.*;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalStateV2;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
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

    @Inject
    public RequirementServiceV2(RequirementRepository requirementRepository,
                              Provider<SearchCommandV2> searchCommandProvider,
                              SearchFilterCommandV2 searchFilterCommand) {

        this.requirementRepository = requirementRepository;
        this.searchFilterCommand = searchFilterCommand;
        this.searchCommandProvider = searchCommandProvider;
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

}
