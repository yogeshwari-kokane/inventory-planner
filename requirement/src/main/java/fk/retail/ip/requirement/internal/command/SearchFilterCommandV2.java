package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.core.repository.GroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

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
        List<String> allFsns;
        if(filterOnGroup(filters)) {
            allFsns = getGroupFsns(filters.get("group").toString());
        }
        else {
            allFsns = groupFsnRepository.getAllFsns();
        }
        List<String> fsns = (List<String>) filters.get("fsns");
        getFsnsIntersection(allFsns,fsns);

        if(filterOnCategory(filters)) {
            List<String> productInfoFsns = getProductInfoFsns(filters);
            getFsnsIntersection(allFsns, productInfoFsns);
        }

        return allFsns;
    }

    private boolean filterOnCategory(Map<String, Object> filters) {

        return (StringUtils.isNotBlank((String)filters.get("vertical"))  ||
                StringUtils.isNotBlank((String)filters.get("category")) ||
                StringUtils.isNotBlank((String)filters.get("superCategory")) ||
                StringUtils.isNotBlank((String)filters.get("businessUnit")));

    }

    private List<String> getProductInfoFsns(Map<String, Object> filters) {
        String vertical = (StringUtils.isNotBlank((String)filters.get("vertical")))?
                filters.get("vertical").toString():null;
        String category = (StringUtils.isNotBlank((String)filters.get("category")))?
                filters.get("category").toString():null;
        String superCategory = (StringUtils.isNotBlank((String)filters.get("superCategory")))?
                filters.get("superCategory").toString():null;
        String businessUnit = (StringUtils.isNotBlank((String)filters.get("businessUnit")))?
                filters.get("businessUnit").toString():null;
        return productInfoRepository.getFsns(vertical, category, superCategory, businessUnit);
    }

    private  List<String> getGroupFsns(String group) {
        List<String> groupFsns = Lists.newArrayList();
        groupFsns.addAll(groupFsnRepository.getFsns(group));
        return groupFsns;
    }

    private boolean filterOnGroup(Map<String, Object> filters) {
        if(StringUtils.isNotBlank(filters.get("group").toString())) {
            return true;
        }
        return false;
    }

    private void getFsnsIntersection(List<String> fsns, List<String> otherFsns) {
        if (otherFsns != null && !otherFsns.isEmpty()) {
            fsns.retainAll(otherFsns);
        }
    }


}
