package fk.retail.ip.fdp.model;

import lombok.Data;
import java.util.List;
import com.google.common.collect.Lists;
/**
 * Created by yogeshwari.k on 16/03/17.
 */
@Data
public class BatchFdpEventEntityPayload<T,V> implements FdpPayload {
    List<FdpEntityPayload<T>> requirementEntity = Lists.newArrayList();
    List<FdpEventPayload<V>> requirementEvent = Lists.newArrayList();
}
