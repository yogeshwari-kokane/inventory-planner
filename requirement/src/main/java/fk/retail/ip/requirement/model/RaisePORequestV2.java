package fk.retail.ip.requirement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * Created by yogeshwari.k on 25/05/17.
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RaisePORequestV2 {

    private List<String> fsns;
    private boolean forward;
    private Map<String, Object> filters;
    private boolean all;

}
