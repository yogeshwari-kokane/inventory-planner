package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.Group;
import fk.retail.ip.requirement.internal.entities.GroupFsn;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.List;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class GroupFsnRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    GroupFsnRepository groupFsnRepository;

    @Test
    public void testFindByFsns() {
        Group group = TestHelper.getGroup("Test Group");
        GroupFsn groupFsn = TestHelper.getGroupFsn("fsn1", group);
        groupFsnRepository.persist(groupFsn);
        List<GroupFsn> groupFsns = groupFsnRepository.findByFsns(Sets.newHashSet("fsn1"));
        Assert.assertEquals(1, groupFsns.size());
        Assert.assertEquals(groupFsn, groupFsns.get(0));
        Assert.assertEquals(group, groupFsns.get(0).getGroup());
    }

    @Test
    public void testGetFsnsByGroup() {
        Group group = TestHelper.getGroup("Test_Group");
        GroupFsn groupFsn = TestHelper.getGroupFsn("fsn1", group);
        GroupFsn groupFsn1 = TestHelper.getGroupFsn("fsn2", group);
        groupFsnRepository.persist(groupFsn);
        groupFsnRepository.persist(groupFsn1);
        List<String> fsns = groupFsnRepository.getFsns("Test_Group");
        Assert.assertEquals(2,fsns.size());
        Assert.assertEquals("fsn1", fsns.get(0));
        Assert.assertEquals("fsn2", fsns.get(1));
    }
}
