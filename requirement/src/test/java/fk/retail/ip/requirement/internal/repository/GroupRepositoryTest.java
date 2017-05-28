package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;

import fk.retail.ip.core.repository.GroupRepository;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.core.entities.IPGroup;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.List;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by nidhigupta.m on 27/04/17.
 */

@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class GroupRepositoryTest  extends TransactionalJpaRepositoryTest {

    @Inject
    GroupRepository groupRepository;

    @Test
    public void testGetGroupsForSegmentation() {
        groupRepository.persist(TestHelper.getGroup("g1"));
        groupRepository.persist(TestHelper.getUnsegmentedGroup("unsegmented_group"));
        List<IPGroup> groupList = groupRepository.getGroupsForSegmentation();
        Assert.assertEquals(1,groupList.size());
        Assert.assertEquals("g1", groupList.get(0).getName());
    }
}
