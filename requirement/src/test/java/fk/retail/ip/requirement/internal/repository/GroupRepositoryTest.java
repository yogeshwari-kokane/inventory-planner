package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.Group;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by yogeshwari.k on 17/05/17.
 */
@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class GroupRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    GroupRepository groupRepository;

    @Test
    public void findByGroupNamesTest() {
        Group group1 = TestHelper.getGroup("dummy_group_1", true);
        Group group2 = TestHelper.getGroup("dummy_group_2", true);
        groupRepository.persist(group1);
        groupRepository.persist(group2);
        List<Group> groupList = groupRepository.findByGroupNames(new HashSet<String>(Arrays.asList("dummy_group_1", "dummy_group_2")));
        Assert.assertEquals(group1, groupList.get(0));
        Assert.assertEquals(group2, groupList.get(1));
    }


}
