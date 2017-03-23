package fk.retail.ip.fdp.model;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSnakeCase
public class FdpRequirementEventPayload {
    String eventType;
    String user;
    String requirementId;
    String attribute;
    String oldValue;
    String newValue;
    String reason;
}
