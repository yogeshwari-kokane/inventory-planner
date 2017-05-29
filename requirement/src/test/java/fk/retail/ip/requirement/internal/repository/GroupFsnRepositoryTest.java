package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import fk.retail.ip.core.MailSender;
import fk.retail.ip.core.entities.GroupFsn;
import fk.retail.ip.core.entities.IPGroup;
import fk.retail.ip.core.repository.GroupFsnRepository;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;

@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class GroupFsnRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    GroupFsnRepository groupFsnRepository;

    @Test
    public void testFindByFsns() {
        IPGroup group = TestHelper.getEnabledGroup("Test IPGroup");
        GroupFsn groupFsn = TestHelper.getGroupFsn("fsn1", group);
        groupFsnRepository.persist(groupFsn);
        List<GroupFsn> groupFsns = groupFsnRepository.findByFsns(Sets.newHashSet("fsn1"));
        Assert.assertEquals(1, groupFsns.size());
        Assert.assertEquals(groupFsn, groupFsns.get(0));
        Assert.assertEquals(group, groupFsns.get(0).getGroup());
    }

    @Test
    public void testGetFsnsByGroup() {
        IPGroup group = TestHelper.getEnabledGroup("Test_Group");
        GroupFsn groupFsn = TestHelper.getGroupFsn("fsn1", group);
        GroupFsn groupFsn1 = TestHelper.getGroupFsn("fsn2", group);
        groupFsnRepository.persist(groupFsn);
        groupFsnRepository.persist(groupFsn1);
        List<String> fsns = groupFsnRepository.getFsns("Test_Group");
        Assert.assertEquals(2,fsns.size());
        Assert.assertEquals("fsn1", fsns.get(0));
        Assert.assertEquals("fsn2", fsns.get(1));
    }

    @Test
    public void testGetAllFsns() {
        IPGroup group = TestHelper.getEnabledGroup("Test_Group");
        IPGroup disabledGroup = TestHelper.getDisabledGroup("Test_Group_Diabled");
        GroupFsn groupFsn = TestHelper.getGroupFsn("fsn1", group);
        GroupFsn groupFsn1 = TestHelper.getGroupFsn("fsn2", disabledGroup);
        groupFsnRepository.persist(groupFsn);
        groupFsnRepository.persist(groupFsn1);
        List<String> fsns = groupFsnRepository.getAllFsns();
        Assert.assertEquals(1,fsns.size());
        Assert.assertEquals("fsn1", fsns.get(0));

    }

    @Test
    public void testUpdateGroupFsn(){
        IPGroup group = TestHelper.getGroup("group1");
        groupFsnRepository.persist(new GroupFsn("fsn1", group, new Date()));
        groupFsnRepository.persist(new GroupFsn("fsn2", group, new Date()));
        groupFsnRepository.updateGroupFsns(group, Arrays.asList("fsn3"));
        List<String> fsns = groupFsnRepository.getFsns("group1");
        Assert.assertEquals(1, fsns.size());
        Assert.assertEquals("fsn3", fsns.get(0));
    }



    @Test
    public void testMail() throws Exception {
        MailSender.sendMail("nidhigupta.m@flipkart.com", "nidhigupta.m@flipkart.com", "nidhigupta.m@flipkart.com", "localhost", "Failed Segmentation Groups","hello",null );
    }
}
