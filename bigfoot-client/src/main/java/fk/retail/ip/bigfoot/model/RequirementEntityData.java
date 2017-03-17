package fk.retail.ip.bigfoot.model;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSnakeCase
public class RequirementEntityData {
    String requirementId;
    String partyId;
    String fsn;
    String warehouse;
    String forecast;
    Integer quantity;
    String supplier;
    Integer app;
    Integer mrp;
    String currency;
    DateTime requiredByDate;
    Integer inventoryQty;
    Integer pendingPOQty;
    Integer openReqQty;
    Integer iwitIntransitQty;
    String state;
    Integer poId;
    Boolean enabled;
    Date createdAt;
    Date updatedAt;
    String policyIds; //structure??
}
