package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.FsnSales;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import javax.inject.Provider;
import javax.persistence.EntityManager;

/**
 * Created by nidhigupta.m on 27/04/17.
 */
public class JPAFsnSalesRepository extends SimpleJpaGenericRepository<FsnSales,Long> implements FsnSalesRepository {

    @Inject
    public JPAFsnSalesRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }
}
