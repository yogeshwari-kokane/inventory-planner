package fk.retail.ip.fdp.model;

import lombok.Data;

/**
 * Created by yogeshwari.k on 24/03/17.
 */
@Data
public class FdpRequirementEventData {
    String user;
    String requirementId;
    String attribute;
    String oldValue;
    String newValue;
    String reason;
}
