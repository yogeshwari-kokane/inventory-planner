package fk.retail.ip.requirement.model;

import com.google.common.collect.Lists;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by yogeshwari.k on 10/05/17.
 */
@Data
@NoArgsConstructor
@JsonSnakeCase
public class SearchResponseV2 {
    Long groupId;
    String groupName;
    String fsn;
    String currentState;
    Integer pvBand;
    Integer salesBand;
    String title;
    String analyticalVertical;
    String category;
    String superCategory;
    String brand;
    Integer fsp;
    List<RequirementSearchV2LineItem> requirements;

    public SearchResponseV2(List<RequirementSearchV2LineItem> requirementSearchLineItems) {
        //set top level
        fsn = requirementSearchLineItems.get(0).getFsn();
        requirements = Lists.newArrayList(requirementSearchLineItems);
    }


    @Data
    @JsonSnakeCase
    @NoArgsConstructor
    public static class GroupedResponse {
        List<SearchResponseV2> groupedRequirements = Lists.newArrayList();
        long totalFsns;
        int pageNo;
        int pageSize;
        public GroupedResponse(long totalFsns, int pageNo, int pageSize) {
            this.totalFsns = totalFsns;
            this.pageNo = pageNo;
            this.pageSize = pageSize;
        }
    }
}


