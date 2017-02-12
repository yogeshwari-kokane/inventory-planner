package fk.retail.ip.requirement.model;

import lombok.Getter;

/**
 * Created by agarwal.vaibhav on 06/02/17.
 */
@Getter
public class UploadRequirementRequest {

    Integer quantity;
    Integer sla;
    String supplier;
    String comment;

}
