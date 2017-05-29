package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import fk.retail.ip.core.repository.GroupRepository;
import fk.retail.ip.core.entities.IPGroup;
import java.util.List;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by nidhigupta.m on 27/04/17.
 */

@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class GroupRepositoryTest  extends TransactionalJpaRepositoryTest {

    @Inject
    GroupRepository groupRepository;

    @Test
    public void findByGroupNamesTest() {
        IPGroup group1 = TestHelper.getGroup("dummy_group_1", true);
        IPGroup group2 = TestHelper.getGroup("dummy_group_2", true);
        groupRepository.persist(group1);
        groupRepository.persist(group2);
        List<IPGroup> groupList = groupRepository.findByGroupNames(new HashSet<String>(Arrays.asList("dummy_group_1", "dummy_group_2")));
        Assert.assertEquals(group1, groupList.get(0));
        Assert.assertEquals(group2, groupList.get(1));
    }

    public void testGetGroupsForSegmentation() {
        groupRepository.persist(TestHelper.getGroup("g1"));
        groupRepository.persist(TestHelper.getUnsegmentedGroup("unsegmented_group"));
        List<IPGroup> groupList = groupRepository.getGroupsForSegmentation();
        Assert.assertEquals(1,groupList.size());
        Assert.assertEquals("g1", groupList.get(0).getName());
    }

}
