package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.repository.GroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yogeshwari.k on 10/05/17.
 */
@Slf4j
public class SearchFilterCommandV2 {

    private final ProductInfoRepository productInfoRepository;
    private final GroupFsnRepository groupFsnRepository;

    @Inject
    public SearchFilterCommandV2(ProductInfoRepository productInfoRepository, GroupFsnRepository groupFsnRepository) {
        this.productInfoRepository = productInfoRepository;
        this.groupFsnRepository = groupFsnRepository;
    }

    public List<String> getSearchFilterFsns(Map<String, Object> filters) {
        List<String> allFsns = groupFsnRepository.getAllFsns();
        List<String> fsns = (List<String>) filters.get("fsns");
        getFsnsIntersection(allFsns,fsns);

        if(filterOnCategory(filters)) {
            List<String> productInfoFsns = getProductInfoFsns(filters);
            getFsnsIntersection(allFsns, productInfoFsns);
        }

        List<String> groupFsns = getGroupFsns(filters);
        getFsnsIntersection(allFsns, groupFsns);
        return allFsns;
    }

    private boolean filterOnCategory(Map<String, Object> filters) {

        return ((filters.containsKey("vertical") && !filters.get("vertical").toString().isEmpty())  ||
                (filters.containsKey("category") && !filters.get("category").toString().isEmpty()) ||
                (filters.containsKey("superCategory") && !filters.get("superCategory").toString().isEmpty()) ||
                (filters.containsKey("businessUnit") && !filters.get("businessUnit").toString().isEmpty()));

    }

    private List<String> getProductInfoFsns(Map<String, Object> filters) {
        String vertical = (filters.containsKey("vertical") && !filters.get("vertical").toString().isEmpty())?
                filters.get("vertical").toString():null;
        String category = (filters.containsKey("category") && !filters.get("category").toString().isEmpty())?
                filters.get("category").toString():null;
        String superCategory = (filters.containsKey("superCategory") && !filters.get("superCategory").toString().isEmpty())?
                filters.get("superCategory").toString():null;
        String businessUnit = (filters.containsKey("businessUnit") && !filters.get("businessUnit").toString().isEmpty())?
                filters.get("businessUnit").toString():null;
        return productInfoRepository.getFsns(vertical, category, superCategory, businessUnit);
    }

    private  List<String> getGroupFsns(Map<String, Object> filters) {
        String group = (String) filters.get("group");
        List<String> groupFsns = Lists.newArrayList();
        if(group != null && !group.isEmpty()) {
            groupFsns.addAll(groupFsnRepository.getFsns(group));
        }
        return  groupFsns;
    }

    private void getFsnsIntersection(List<String> fsns, List<String> otherFsns) {
        if (otherFsns != null && !otherFsns.isEmpty()) {
            fsns.retainAll(otherFsns);
        }
    }


}
