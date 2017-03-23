package fk.retail.ip.fdp.model;

import java.util.Date;
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
public class FdpRequirementEntityPayload {
    String entityId;
    Date updatedAt;
    String schemaVersion;
    FdpRequirementEntityData data;
}
