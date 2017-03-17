package fk.retail.ip.requirement.model;

import fk.retail.ip.requirement.internal.entities.Requirement;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSnakeCase
public class RequirementChangeRequest {
    Requirement requirement;
    List<ChangeMap> changeMaps;
}
