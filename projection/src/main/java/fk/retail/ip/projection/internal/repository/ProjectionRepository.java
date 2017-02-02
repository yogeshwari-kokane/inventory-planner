package fk.retail.ip.projection.internal.repository;

import fk.retail.ip.projection.internal.entities.Projection;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Created by nidhigupta.m on 07/01/17.
 */
public class ProjectionRepository extends SimpleJpaGenericRepository<Projection, Long> {

    public ProjectionRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    public Projection getProjectionById(Long id) {
        Query query = getEntityManager().createNamedQuery("Projection.findById", Projection.class).
                setParameter("id", id);
        return (Projection) query.getSingleResult();
    }

}
