package fk.retail.ip.requirement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Pragalathan M<pragalathan.m@flipkart.com>
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequirementApprovalRequest {

    @JsonProperty("projections")
    private Projection projection;

    public String getAction() {
        return projection.getAction();
    }

    public String getState() {
        return projection.getState();
    }

    public boolean isAll() {
        return projection.isAll();
    }

    public Integer[] getIds() {
        return projection.getIds();
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Projection {

        @JsonProperty("projection_action")
        private String action;
        private String state;
        private String international;
        @JsonProperty("rank_from")
        private Integer rankFrom;
        @JsonProperty("rank_to")
        private Integer rankTo;
        @JsonProperty("price_from")
        private Integer priceFrom;
        @JsonProperty("price_to")
        private Integer priceTo;
        private boolean all;
        private int page;
        @JsonProperty("per_page")
        private int pageSize;
        @JsonProperty("id")
        private Integer[] ids;
    }
}
