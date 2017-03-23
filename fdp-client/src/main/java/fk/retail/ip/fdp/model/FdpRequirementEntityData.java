package fk.retail.ip.fdp.model;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    DateTime requiredByDate;
    int inventoryQty;
    int pendingPOQty;
    int openReqQty;
    int iwitIntransitQty;
    String state;
    String poId;
    Boolean enabled;
    Date createdAt;
    Date updatedAt;
    String policyIds; //structure??
}
