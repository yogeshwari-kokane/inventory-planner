package fk.retail.ip.projection.internal.repository;

import java.util.List;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import fk.retail.ip.projection.internal.entities.Projection;
import fk.retail.ip.projection.internal.entities.ProjectionItem;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;

/**
 * Created by nidhigupta.m on 09/01/17.
 */
public class ProjectionItemRepository extends SimpleJpaGenericRepository<ProjectionItem, Long> {

    public ProjectionItemRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    public List<ProjectionItem> getProjectionByIds(List<Long> ids) {
        Query query = getEntityManager().createNamedQuery("ProjectionItem.findByIds", ProjectionItem.class).
                setParameter("id", ids);
        return query.getResultList();
    }
}
