package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.Group;
import fk.sp.common.extensions.jpa.JpaGenericRepository;

import java.util.List;
import java.util.Set;

public interface GroupRepository extends JpaGenericRepository<Group, Long> {

    List<Group> findByGroupNames(Set<String> groupNames);

}
