package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import fk.retail.ip.fdp.model.BatchFdpRequirementEventEntityPayload;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
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

        BatchFdpRequirementEventEntityPayload batchFdpRequirementEventEntityPayload;
        FdpRequirementIngestorHelper fdpRequirementIngestorHelper = new FdpRequirementIngestorHelper();
        List<RequirementChangeRequest> fdpRequests = Lists.newArrayList();
        RequirementChangeRequest requirementChangeRequest = new RequirementChangeRequest();
        List<ChangeMap> changeMaps = Lists.newArrayList();
        requirementChangeRequest.setRequirement(requirement);
        changeMaps.add(createChangeMap("Sla", requirement.getSla().toString(),"20","CDO_SLA_OVERRIDE", "SLA overridden by CDO", "dummy_user"));
        requirementChangeRequest.setChangeMaps(changeMaps);
        fdpRequests.add(requirementChangeRequest);
        //batchFdpRequirementEventEntityPayload = fdpRequirementIngestorHelper.pushToFdp(fdpRequests);
    }

    private ChangeMap createChangeMap(String attribute, String oldValue, String newValue, String eventType, String reason, String user){
        ChangeMap changeMap = new ChangeMap();
        changeMap.setAttribute(attribute);
        changeMap.setOldValue(oldValue);
        changeMap.setNewValue(newValue);
        changeMap.setEventType(eventType);
        changeMap.setReason(reason);
        changeMap.setUser(user);
        return changeMap;
    }
}
