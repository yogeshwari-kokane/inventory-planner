package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.RequirementApprovalTransitionV2;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * Created by yogeshwari.k on 19/05/17.
 */
public class JPARequirementApprovalTransitionRepositoryV2 extends SimpleJpaGenericRepository<RequirementApprovalTransitionV2, Long>
        implements RequirementApprovalTransitionRepositoryV2 {

    @Inject
    public JPARequirementApprovalTransitionRepositoryV2(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }


    @Override
    public List<RequirementApprovalTransitionV2> getApprovalTransition(String fromState, boolean forward) {
        TypedQuery<RequirementApprovalTransitionV2> query = getEntityManager().
                createNamedQuery("RequirementApprovalTransitionV2.getApprovalStateTransition",RequirementApprovalTransitionV2.class);
        query.setParameter("fromState", fromState);
        query.setParameter("forward", forward);
        return query.getResultList();

    }

}
