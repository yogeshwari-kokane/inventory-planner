package fk.retail.ip.fdp.model;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import java.util.List;
import java.util.Date;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Data
@JsonSnakeCase
public class FdpRequirementEntityData {
    String requirementId;
    String partyId;
    String fsn;
    String warehouse;
    String forecast;
    double quantity;
    String supplier;
    int app;
    int mrp;
    String currency;
    Date requiredByDate;
    int inventoryQty;
    int pendingPOQty;
    int openReqQty;
    int iwitIntransitQty;
    String state;
    String poId;
    Boolean enabled;
    Date createdAt;
    Date updatedAt;
    List<String> policyIds;
}
