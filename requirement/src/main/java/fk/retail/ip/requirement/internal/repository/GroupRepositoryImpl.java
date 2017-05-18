package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import fk.retail.ip.requirement.internal.entities.Group;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;


public class GroupRepositoryImpl extends SimpleJpaGenericRepository<Group, Long>
        implements GroupRepository {

    @Inject
    public GroupRepositoryImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<Group> findByGroupNames(Collection<String> groupNames) {
        if (CollectionUtils.isEmpty(groupNames)) {
            return Lists.newArrayList();
        }
        TypedQuery<Group> query = getEntityManager().createNamedQuery("Group.findByGroupNames",Group.class);
        query.setParameter("groupNames", groupNames);
        return query.getResultList();
    }

}
