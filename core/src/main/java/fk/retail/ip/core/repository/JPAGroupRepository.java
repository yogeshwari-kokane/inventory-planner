package fk.retail.ip.core.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import fk.retail.ip.core.entities.IPGroup;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * Created by nidhigupta.m on 24/04/17.
 */
public class JPAGroupRepository extends SimpleJpaGenericRepository<IPGroup, Long> implements GroupRepository {

    @Inject
    public JPAGroupRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }


    @Override
    public List<IPGroup> getGroupsForSegmentation() {
        TypedQuery<IPGroup> groupForSegmentationQuery = getEntityManager().createNamedQuery("IPGroup.getGroupsForSegmentation", IPGroup.class);
        return groupForSegmentationQuery.getResultList();
    }

    @Override
    public IPGroup createGroup(String groupName) {
        IPGroup ipGroup = new IPGroup();
        ipGroup.setName(groupName);
        ipGroup.setEnabled(true);
        ipGroup.setCreatedAt(new Date());
        ipGroup.setRule(null);
        ipGroup.setSegmentationEnabled(false);
        this.persist(ipGroup);
        return ipGroup;
    }

    @Override
    public List<IPGroup> findByGroupNames(Collection<String> groupNames) {
        if (CollectionUtils.isEmpty(groupNames)) {
            return Lists.newArrayList();
        }
        TypedQuery<IPGroup> query = getEntityManager().createNamedQuery("Group.findByGroupNames",IPGroup.class);
        query.setParameter("groupNames", groupNames);
        return query.getResultList();
    }


    @Override
    public IPGroup findByGroupName(String groupName) {
        TypedQuery<IPGroup> groupQuery = getEntityManager().createNamedQuery("IPGroup.getGroupsByName",IPGroup.class);
        groupQuery.setParameter("name", groupName);
        return groupQuery.getSingleResult();
    }

    @Override
    public List<IPGroup> getEnabledGroups() {
        TypedQuery<IPGroup> enabledGroupQuery = getEntityManager().createNamedQuery("IPGroup.getEnabledGroup", IPGroup.class);
        return enabledGroupQuery.getResultList();
    }

    @Override
    public List<IPGroup> getStaticGroups() {
        TypedQuery<IPGroup> staticGroupQuery = getEntityManager().createNamedQuery("IPGroup.getStaticGroup", IPGroup.class);
        return staticGroupQuery.getResultList();
    }


}
