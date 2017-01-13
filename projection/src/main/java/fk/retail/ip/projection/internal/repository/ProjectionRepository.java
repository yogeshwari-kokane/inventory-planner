package fk.retail.ip.projection.internal.repository;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import fk.retail.ip.projection.internal.entities.Projection;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;

/**
 * Created by nidhigupta.m on 07/01/17.
 */
public class ProjectionRepository extends SimpleJpaGenericRepository<Projection, Long> {

    public ProjectionRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }




}
