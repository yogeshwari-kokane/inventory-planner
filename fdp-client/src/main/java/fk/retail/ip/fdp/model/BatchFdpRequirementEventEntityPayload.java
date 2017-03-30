package fk.retail.ip.fdp.model;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import java.util.List;
import com.google.common.collect.Lists;
/**
 * Created by yogeshwari.k on 16/03/17.
 */
@Data
@JsonSnakeCase
public class BatchFdpRequirementEventEntityPayload implements FdpPayload {
    List<FdpEntityPayload<FdpRequirementEntityData>> requirementEntity = Lists.newArrayList();
    List<FdpEventPayload<FdpRequirementEventData>> requirementEvent = Lists.newArrayList();
}
