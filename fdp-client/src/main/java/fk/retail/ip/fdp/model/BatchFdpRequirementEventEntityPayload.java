package fk.retail.ip.fdp.model;

import lombok.Data;
import java.util.List;
import com.google.common.collect.Lists;
/**
 * Created by yogeshwari.k on 16/03/17.
 */
@Data
public class BatchFdpRequirementEventEntityPayload implements FdpPayload {
    List<FdpEntityPayload<FdpRequirementEntityData>> PurchaseRequirementEntity = Lists.newArrayList();
    List<FdpEventPayload<FdpRequirementEventData>> PurchaseRequirementEvent = Lists.newArrayList();
}
