package fk.retail.ip.core.repository;

import fk.retail.ip.core.entities.GroupFsn;
import fk.retail.ip.core.entities.IPGroup;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface GroupFsnRepository extends JpaGenericRepository<GroupFsn, Long> {
    List<GroupFsn> findByFsns(Set<String> fsns);

    List<String> getFsns(String group);

    List<String> getAllFsns();

    List<GroupFsn> findByGroupIds(Set<Long> groupIds);

    void updateGroupFsns(IPGroup group, List<String> fsns);

    Date fetchCreatedAt(IPGroup group);
    void insertFsnsForGroup(IPGroup group, List<String> fsns);
}
