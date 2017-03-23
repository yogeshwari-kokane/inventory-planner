package fk.retail.ip.fdp.model;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.google.common.collect.Lists;
/**
 * Created by yogeshwari.k on 16/03/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSnakeCase
public class BatchFdpRequirementEventEntityPayload {
    List<FdpRequirementEntityPayload> requirementEntity = Lists.newArrayList();;
    List<FdpRequirementEventPayload> requirementEvent = Lists.newArrayList();
}
