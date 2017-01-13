package fk.retail.ip.projection.internal.repository;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import fk.retail.ip.projection.internal.entities.OverrideEvent;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;

/**
 * Created by nidhigupta.m on 10/01/17.
 */
public class OverrideEventRepository extends SimpleJpaGenericRepository<OverrideEvent, Long>{
    public OverrideEventRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    public void persistOverrideEvent(Long entityId, String fsn, String warehouse, String fieldName, String oldValue, String newValue, String overrideReason, String overrideType,String overrideEntity){
        OverrideEvent overrideEvent = new OverrideEvent(entityId, fsn, warehouse, fieldName, oldValue, newValue, overrideReason, overrideType, overrideEntity);
        getEntityManager().persist(overrideEvent);
    }
}
