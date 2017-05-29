package fk.retail.ip.core.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import fk.retail.ip.core.entities.GroupFsn;
import fk.retail.ip.core.entities.IPGroup;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;

public class JPAGroupFsnRepository extends SimpleJpaGenericRepository<GroupFsn, Long> implements GroupFsnRepository {

    @Inject
    public JPAGroupFsnRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<GroupFsn> findByFsns(Set<String> fsns) {
        if (CollectionUtils.isEmpty(fsns)) {
            return Lists.newArrayList();
        }
        TypedQuery<GroupFsn> query = getEntityManager()
                .createNamedQuery("GroupFsn.fetchByFsns", GroupFsn.class);
        query.setParameter("fsns", fsns);
        return query.getResultList();
    }

    @Override
    public List<String> getFsns(String group) {
        TypedQuery<String> query = getEntityManager().createNamedQuery("GroupFsn.getFsnsForGroup",String.class);
        query.setParameter("groupName", group);
        return query.getResultList();
    }

    @Override
    public List<String> getAllFsns() {
        TypedQuery<String> query = getEntityManager().createNamedQuery("GroupFsn.getDistinctFsns",String.class);
        return query.getResultList();
    }

    @Override
    public void updateGroupFsns(IPGroup group, List<String> fsns) {
        Query deleteGroupFsnQuery = getEntityManager().createNamedQuery("GroupFsn.deleteByGroup");
        deleteGroupFsnQuery.setParameter("id", group.getId());
        deleteGroupFsnQuery.executeUpdate();
       insertFsnsForGroup(group, fsns);
    }

    @Override
    public void insertFsnsForGroup(IPGroup group, List<String> fsns) {
        Date date = new Date();
        fsns.forEach(fsn -> {
            GroupFsn groupFsn = new GroupFsn(fsn, group, date);
            this.persist(groupFsn);
        });
    }

    @Override
    public Date fetchCreatedAt(IPGroup group) {
        TypedQuery<Date> query = getEntityManager().createNamedQuery("GroupFsn.getLastCreatedAt", Date.class);
        query.setParameter("id", group.getId());
        query.setMaxResults(1);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<GroupFsn> findByGroupIds(Set<Long> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return Lists.newArrayList();
        }
        TypedQuery<GroupFsn> query = getEntityManager().createNamedQuery("GroupFsn.findByGroupIds",GroupFsn.class);
        query.setParameter("ids", groupIds);
        return query.getResultList();
    }
}
