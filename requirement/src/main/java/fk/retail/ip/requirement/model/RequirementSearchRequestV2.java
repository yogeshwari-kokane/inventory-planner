package fk.retail.ip.requirement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * Created by yogeshwari.k on 09/05/17.
 */
@Getter
@Setter
@ToString
@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequirementSearchRequestV2 {

    private Map<String, Object> filters;
    int page;
    int pageSize;
}
