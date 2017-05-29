package fk.retail.ip.core.repository;

import java.util.Collection;
import java.util.List;

import fk.retail.ip.core.entities.IPGroup;
import fk.sp.common.extensions.jpa.JpaGenericRepository;

/**
 * Created by nidhigupta.m on 24/04/17.
 */
public interface GroupRepository extends JpaGenericRepository<IPGroup, Long> {

    List<IPGroup> getGroupsForSegmentation();

    IPGroup createGroup(String groupName);

    IPGroup findByGroupName(String groupName);

    List<IPGroup> getEnabledGroups();

    List<IPGroup> getStaticGroups();

    List<IPGroup> findByGroupNames(Collection<String> groupNames);
}
