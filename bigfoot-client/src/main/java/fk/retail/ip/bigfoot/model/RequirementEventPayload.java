package fk.retail.ip.bigfoot.model;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSnakeCase
public class RequirementEventPayload {
    String eventType;
    String user;
    String requirementId;
    String attribute;
    String oldValue;
    String newValue;
    String reason;
}
