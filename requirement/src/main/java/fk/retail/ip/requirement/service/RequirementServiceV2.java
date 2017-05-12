package fk.retail.ip.requirement.service;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.command.*;
import fk.retail.ip.requirement.internal.entities.Requirement;
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
        Integer pageNo = request.getFilters().get("page")!=null ? Integer.parseInt(request.getFilters().get("page").toString()): 1;
        Integer pageSize = request.getFilters().get("page_size")!=null ?
                Integer.parseInt(request.getFilters().get("page_size").toString()):20;
        String state = (String) request.getFilters().get("state");
        String group = (String) request.getFilters().get("group");
        List<String> fsns = searchFilterCommand.getSearchFilterFsns(request.getFilters());
        if(fsns == null || fsns.isEmpty()) return new SearchResponseV2.GroupedResponse(0, pageNo, pageSize);
        log.info("Start: get state fsns based on page_size");
        List <String> stateFsns = requirementRepository.findFsnsByStateFsns(state, fsns, pageNo, pageSize);
        log.info("Finish: get state fsns based on page_size");
        if(stateFsns == null || stateFsns.isEmpty()) return new SearchResponseV2.GroupedResponse(0, pageNo, pageSize);
        log.info("Start: get requirements for fsns");
        List<Requirement> requirements = requirementRepository.findCurrentRequirementsByStateFsns(state, stateFsns);
        log.info("Finish: get requirements for fsns");
        log.info("Search Request for {} number of requirements", requirements.size());
        Map<String, SearchResponseV2> fsnToSearchItemsMap =  searchCommandProvider.get().execute(requirements, state, group);
        List<SearchResponseV2> searchResponses = fsnToSearchItemsMap.entrySet().stream().map(s -> s.getValue()).collect(Collectors.toList());
        SearchResponseV2.GroupedResponse groupedResponse = new SearchResponseV2.GroupedResponse(stateFsns.size(), pageNo, pageSize);
        groupedResponse.setGroupedRequirements(searchResponses);
        return groupedResponse;
    }

}
