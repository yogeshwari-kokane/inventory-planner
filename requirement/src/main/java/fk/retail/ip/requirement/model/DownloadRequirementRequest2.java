package fk.retail.ip.requirement.model;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by yogeshwari.k on 18/05/17.
 */
@Data
@JsonSnakeCase
public class DownloadRequirementRequest2 {

    List<String> fsns;
    boolean lastAppSupplierRequired;
    Map<String, Object> filters;
    boolean all;

}
