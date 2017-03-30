package fk.retail.ip.fdp.model;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

/**
 * Created by yogeshwari.k on 24/03/17.
 */
@Data
@JsonSnakeCase
public class FdpRequirementEventData {
    String user;
    String requirementId;
    String attribute;
    String oldValue;
    String newValue;
    String reason;
    String eventType;
}
