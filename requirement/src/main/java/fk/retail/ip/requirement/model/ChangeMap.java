package fk.retail.ip.requirement.model;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeMap {
    String attribute;
    String oldValue;
    String newValue;
    String reason;
    String eventType;
    String user;
}
