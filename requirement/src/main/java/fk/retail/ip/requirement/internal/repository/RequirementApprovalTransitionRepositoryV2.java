package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.RequirementApprovalTransition;
import fk.retail.ip.requirement.internal.entities.RequirementApprovalTransitionV2;
import fk.sp.common.extensions.jpa.JpaGenericRepository;

import java.util.List;

/**
 * Created by yogeshwari.k on 19/05/17.
 */
public interface RequirementApprovalTransitionRepositoryV2 extends JpaGenericRepository<RequirementApprovalTransitionV2, Long> {

    List<RequirementApprovalTransitionV2> getApprovalTransition(String fromState, boolean forward);

}
