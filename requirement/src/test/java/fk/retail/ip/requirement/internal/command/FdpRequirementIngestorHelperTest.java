package fk.retail.ip.requirement.internal.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import fk.retail.ip.fdp.model.BatchFdpRequirementEventEntityPayload;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.enums.FdpRequirementEventType;
import fk.retail.ip.requirement.model.ChangeMap;
import fk.retail.ip.requirement.model.RequirementChangeRequest;
import java.util.Date;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

/**
 * Created by yogeshwari.k on 22/03/17.
 */
@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class FdpRequirementIngestorHelperTest {
    ObjectMapper mapper;
    @Test
    public void PayloadCreationTest() throws IOException {
        RequirementSnapshot requirementSnapshot = new RequirementSnapshot();
        requirementSnapshot.setForecast("[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]");
        requirementSnapshot.setInventoryQty(10);
        requirementSnapshot.setPendingPoQty(10);
        requirementSnapshot.setOpenReqQty(10);
        requirementSnapshot.setIwitIntransitQty(10);

        Requirement requirement = new Requirement();
        requirement.setFsn("fsn1");
        requirement.setWarehouse("warehouse1");
        Date date = new Date();
        requirement.setCreatedAt(date);
        requirement.setUpdatedAt(date);
        requirement.setQuantity(100);
        requirement.setRequirementSnapshot(requirementSnapshot);
        requirement.setSupplier("supplier1");
        requirement.setApp(10);
        requirement.setMrp(10);
        requirement.setCurrency("INR");
        requirement.setSla(5);
        requirement.setState("proposed");
        requirement.setEnabled(true);

        //BatchFdpRequirementEventEntityPayload batchFdpRequirementEventEntityPayload;
        FdpRequirementIngestorHelper fdpRequirementIngestorHelper = new FdpRequirementIngestorHelper();
        List<RequirementChangeRequest> bigfootRequests = Lists.newArrayList();
        RequirementChangeRequest requirementChangeRequest = new RequirementChangeRequest();
        List<ChangeMap> changeMaps = Lists.newArrayList();
        requirementChangeRequest.setRequirement(requirement);
        changeMaps.add(fdpRequirementIngestorHelper.createChangeMap("Sla", requirement.getSla().toString(),"20", FdpRequirementEventType.CDO_SLA_OVERRIDE.toString(), "SLA overridden by CDO", "dummy_user"));
        requirementChangeRequest.setChangeMaps(changeMaps);
        bigfootRequests.add(requirementChangeRequest);
        BatchFdpRequirementEventEntityPayload batchFdpRequirementEventEntityPayload = fdpRequirementIngestorHelper.pushToFdp(bigfootRequests);
        String result = mapper.writeValueAsString(batchFdpRequirementEventEntityPayload);
        System.out.println("result:"+result);
    }

}
