package fk.retail.ip.requirement.internal.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.fdp.model.BatchFdpRequirementEventEntityPayload;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.enums.FdpRequirementEventType;
import fk.retail.ip.requirement.model.RequirementChangeMap;
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
    ObjectMapper mapper = new ObjectMapper();

    @Inject
    FdpIngestor fdpRequirementIngestorHelper;

    @Test
    public void PayloadCreationTest() throws IOException {
        List<String> policyIdList1 = Lists.newArrayList();
        String policyId1 = "dummy_group_fsn1_warehouse1_Rop";
        policyIdList1.add(policyId1);
        policyId1 = "dummy_group_fsn1_warehouse1_Roc";
        policyIdList1.add(policyId1);
        String policyIds = String.join(",", policyIdList1);

        RequirementSnapshot requirementSnapshot1 = new RequirementSnapshot();
        requirementSnapshot1.setForecast("[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]");
        requirementSnapshot1.setInventoryQty(10);
        requirementSnapshot1.setPendingPoQty(10);
        requirementSnapshot1.setOpenReqQty(10);
        requirementSnapshot1.setIwitIntransitQty(10);
        requirementSnapshot1.setPolicyIds(policyIds);

        Requirement requirement1 = new Requirement();
        requirement1.setFsn("fsn1");
        requirement1.setWarehouse("warehouse1");
        Date date1 = new Date();
        requirement1.setCreatedAt(date1);
        requirement1.setUpdatedAt(date1);
        requirement1.setQuantity(100);
        requirement1.setRequirementSnapshot(requirementSnapshot1);
        requirement1.setSupplier("supplier1");
        requirement1.setApp(10);
        requirement1.setMrp(10);
        requirement1.setCurrency("INR");
        requirement1.setSla(5);
        requirement1.setState("proposed");
        requirement1.setEnabled(true);


        List<String> policyIdList2 = Lists.newArrayList();
        String policyId2 = "dummy_group_fsn1_warehouse1_Rop";
        policyIdList2.add(policyId2);
        policyId2 = "dummy_group_fsn1_warehouse1_Roc";
        policyIdList2.add(policyId2);
        policyIds = String.join(",", policyIdList2);

        RequirementSnapshot requirementSnapshot2 = new RequirementSnapshot();
        requirementSnapshot2.setForecast("[2,2,2,2,2,2,2,2,2,2,2,2,2,2,2]");
        requirementSnapshot2.setInventoryQty(20);
        requirementSnapshot2.setPendingPoQty(20);
        requirementSnapshot2.setOpenReqQty(20);
        requirementSnapshot2.setIwitIntransitQty(20);
        requirementSnapshot2.setPolicyIds(policyIds);

        Requirement requirement2 = new Requirement();
        requirement2.setFsn("fsn2");
        requirement2.setWarehouse("warehouse2");
        Date date2 = new Date();
        requirement2.setCreatedAt(date2);
        requirement2.setUpdatedAt(date2);
        requirement2.setQuantity(200);
        requirement2.setRequirementSnapshot(requirementSnapshot2);
        requirement2.setSupplier("supplier2");
        requirement2.setApp(20);
        requirement2.setMrp(20);
        requirement2.setCurrency("INR");
        requirement2.setSla(5);
        requirement2.setState("proposed");
        requirement2.setEnabled(true);

        PayloadCreationHelper payloadCreationHelper = new PayloadCreationHelper();
        List<RequirementChangeRequest> fdpRequests = Lists.newArrayList();

        RequirementChangeRequest requirementChangeRequest1 = new RequirementChangeRequest();
        List<RequirementChangeMap> requirementChangeMaps1 = Lists.newArrayList();
        requirementChangeRequest1.setRequirement(requirement1);
        requirementChangeMaps1.add(payloadCreationHelper.createChangeMap("Sla", requirement1.getSla().toString(),"20", FdpRequirementEventType.CDO_SLA_OVERRIDE.toString(), "SLA overridden by CDO", "dummy_user"));
        requirementChangeRequest1.setRequirementChangeMaps(requirementChangeMaps1);
        fdpRequests.add(requirementChangeRequest1);

        RequirementChangeRequest requirementChangeRequest2 = new RequirementChangeRequest();
        List<RequirementChangeMap> requirementChangeMaps2 = Lists.newArrayList();
        requirementChangeRequest2.setRequirement(requirement2);
        requirementChangeMaps2.add(payloadCreationHelper.createChangeMap("Sla", requirement1.getSla().toString(),"20", FdpRequirementEventType.CDO_SLA_OVERRIDE.toString(), "SLA overridden by CDO", "dummy_user"));
        requirementChangeRequest2.setRequirementChangeMaps(requirementChangeMaps2);
        fdpRequests.add(requirementChangeRequest2);

        BatchFdpRequirementEventEntityPayload fdpPayload = fdpRequirementIngestorHelper.pushToFdp(fdpRequests);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String result = mapper.writeValueAsString(fdpPayload);
        System.out.println("result:"+result);

    }

}