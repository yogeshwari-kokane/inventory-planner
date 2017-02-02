package fk.retail.ip.requirement.internal.states;

import java.util.List;

import javax.ws.rs.core.StreamingOutput;

import fk.retail.ip.requirement.internal.entities.Requirement;

/**
 * Created by nidhigupta.m on 30/01/17.
 */
public interface RequirementState {

    StreamingOutput download(List<Requirement> requirementList, boolean isLastAppSupplierRequired, String requirementState);

    void upload();

    void calculate();

}
