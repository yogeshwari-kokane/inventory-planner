package fk.retail.ip.fdp.model;

import java.util.Date;
import lombok.Data;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Data
public class FdpEventPayload<T> {
    Object eventId;
    int schemaVersion;
    String eventType; //in data??
    Date updatedAt;
    T data;
}
