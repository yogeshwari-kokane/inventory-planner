package fk.retail.ip.requirement.internal.command;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.config.TriggerRequirementConfiguration;
import fk.retail.ip.core.entities.GroupFsn;
import fk.retail.ip.core.entities.IPGroup;
import fk.retail.ip.core.repository.GroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.PolicyRepository;
import fk.retail.ip.requirement.internal.repository.TestHelper;
import fk.sp.common.extensions.jpa.Page;
import fk.sp.common.restbus.sender.RestbusMessageSender;
import java.util.Date;
import java.util.List;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)

public class TriggerRequirementCommandTest {

    @Mock
    GroupFsnRepository groupFsnRepository;
    @Mock
    PolicyRepository policyRepository;
    @Mock
    RestbusMessageSender restbusMessageSender;

    TriggerRequirementConfiguration triggerRequirementConfiguration;
    @Inject
    ObjectMapper mapper;
    TriggerRequirementCommand triggerRequirementCommand;

    List<IPGroup> groups;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.triggerRequirementConfiguration = new TriggerRequirementConfiguration();
        triggerRequirementCommand =
                Mockito.spy(new TriggerRequirementCommand(groupFsnRepository, policyRepository,
                                              restbusMessageSender, triggerRequirementConfiguration,
                                              mapper));
        Mockito.when(triggerRequirementCommand.getDate()).thenReturn(new Date(1493200000));
        int totalCount = 10;
        List<GroupFsn> groupFsns = Lists.newArrayList();
        groups = Lists.newArrayList();
        for (int i = 1; i <= 3; i++) {
            IPGroup group = TestHelper.getEnabledGroup("group"+i);
            group.setId((long) i);
            groups.add(group);
        }
        Page<GroupFsn> page = new Page(totalCount, groupFsns);
        for (int i = 1; i <= totalCount; i++) {
            GroupFsn groupFsn = TestHelper.getGroupFsn("fsn" + i, groups.get(i % groups.size()));
            groupFsns.add(groupFsn);
        }
        Mockito.when(groupFsnRepository.findAll(Matchers.any())).thenReturn(page);
    }

    @Test
    public void testExecute() {
        //without policies
        List<String> fsns = triggerRequirementCommand.execute();
        Assert.assertEquals(0,fsns.size());
    }


}
