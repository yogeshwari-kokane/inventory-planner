package fk.retail.ip.bigfoot.model;

import java.util.Date;
import lombok.Getter;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSnakeCase
public class RequirementEntityPayload {
    String entityId;
    Date updatedAt;
    String schemaVersion;
    RequirementEntityData data;
}
