package fk.retail.ip.bigfoot.model;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * Created by yogeshwari.k on 16/03/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSnakeCase
public class BatchBigfootRequirementEventEntityPayload {
    List<RequirementEntityPayload> requirementEntity;
    List<RequirementEventPayload> requirementEvent;
}
