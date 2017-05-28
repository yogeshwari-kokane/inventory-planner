package fk.retail.ip.core.repository;

import com.google.inject.Inject;

import java.util.List;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import fk.retail.ip.core.entities.ProductData;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;

/**
 * Created by nidhigupta.m on 16/05/17.
 */
public class JPAProductDataRepository  extends SimpleJpaGenericRepository<ProductData, Long> implements ProductDataRepository {
    @Inject
    public JPAProductDataRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<String> getDistinctVerticals() {
        TypedQuery<String> query = getEntityManager().createNamedQuery("getDistinctVerticals", String.class);
        return query.getResultList();
    }

    @Override
    public List<String> getFsns(String vertical) {
        TypedQuery<String> query = getEntityManager().createNamedQuery("ProductData.getFsnsByVertical", String.class);
        query.setParameter("vertical",vertical);
        return query.getResultList();
    }
}
