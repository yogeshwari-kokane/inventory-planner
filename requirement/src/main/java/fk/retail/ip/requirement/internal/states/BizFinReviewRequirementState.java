package fk.retail.ip.requirement.internal.states;

import fk.retail.ip.requirement.internal.entities.Requirement;
import java.util.List;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 30/01/17.
 */
public class BizFinReviewRequirementState implements RequirementState{
    @Override
    public StreamingOutput download(List<Requirement> requirementList, boolean isLastAppSupplierRequired, String requirementState) {
        return null;
    }

    @Override
    public void upload() {

    }

    @Override
    public void calculate() {

    }
}
