package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.*;
import fk.retail.ip.requirement.model.RequirementSearchV2LineItem;
import fk.retail.ip.requirement.model.SearchResponseV2;
import fk.retail.ip.zulu.client.ZuluClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by yogeshwari.k on 10/05/17.
 */
@Slf4j
public class SearchCommandV2 extends RequirementSearchDataAggregatorV2{

    @Inject
    public SearchCommandV2(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository,
                           ProductInfoRepository productInfoRepository, ZuluClient zuluClient,
                           RequirementRepository requirementRepository, WarehouseRepository warehouseRepository,
                           GroupFsnRepository groupFsnRepository, GroupRepository groupRepository) {
        super(fsnBandRepository, weeklySaleRepository, productInfoRepository, zuluClient,
                requirementRepository, warehouseRepository, groupFsnRepository, groupRepository);
    }

    public Map<String, SearchResponseV2> execute(List<Requirement> requirements, String state, String groupName) {
        log.info("Search Request for {} number of requirements", requirements.size());
        List<RequirementSearchV2LineItem> requirementSearchLineItems = requirements.stream()
                .map(RequirementSearchV2LineItem::new).collect(toList());
        if(state.equals(RequirementApprovalState.BIZFIN_REVIEW.toString())) {
            MultiKeyMap<String, Integer> fsnWhQuantity = fetchCdoQuantity(requirements);
            requirementSearchLineItems.forEach(reqItem
                    -> {
                reqItem.setQty(fsnWhQuantity.get(reqItem.getFsn(),reqItem.getWarehouse()));
            });
        }

        Map<String, List<RequirementSearchV2LineItem>> fsnToRequirement = requirementSearchLineItems.stream()
                .collect(Collectors.groupingBy(RequirementSearchV2LineItem::getFsn));
        Set<String> fsns = fsnToRequirement.keySet();
        fetchSalesBucketData(fsns, requirementSearchLineItems);
        fetchWarehouseName(requirementSearchLineItems);
        Map<String, SearchResponseV2> fsnToSearchResponse = new HashMap<>();
        for (String fsn : fsnToRequirement.keySet()) {
            List<RequirementSearchV2LineItem> requirementSearchV2LineItemList = fsnToRequirement.get(fsn);
            SearchResponseV2 searchResponseV2 = new SearchResponseV2(requirementSearchV2LineItemList);
            searchResponseV2.setCurrentState(state);
            fsnToSearchResponse.put(fsn, searchResponseV2);
        }

        if (requirements.isEmpty()) {
            log.info("No requirements found for search.");
            return fsnToSearchResponse;
        }
        fetchProductData(fsnToSearchResponse);
        fetchFsnBandData(fsnToSearchResponse);
        fetchGroupData(fsnToSearchResponse, groupName);
        return fsnToSearchResponse;
    }


}
