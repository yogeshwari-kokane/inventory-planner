package fk.retail.ip.requirement.model;

import com.google.common.collect.Lists;
import fk.retail.ip.fdp.config.FdpConfiguration;
import fk.retail.ip.fdp.model.FdpRequirementEventData;
import fk.retail.ip.fdp.model.FdpEventPayload;
import java.util.List;
/**
 * Created by yogeshwari.k on 17/03/17.
 */
public class CreateRequirementEventPayload implements FdpEventMapper<FdpRequirementEventData,List<ChangeMap>> {

    @Override
    public List<FdpEventPayload<FdpRequirementEventData>> convertRequirementToEventPayload(Object requirementId, List<ChangeMap> changeMaps) {
        FdpConfiguration fdpConfiguration = new FdpConfiguration();
        List<FdpEventPayload<FdpRequirementEventData>> fdpRequirementEventPayloadList = Lists.newArrayList();
        changeMaps.forEach(changeMap -> {
            FdpEventPayload fdpRequirementEventPayload =new FdpEventPayload();
            //fdpRequirementEventPayload.setEventId();
            fdpRequirementEventPayload.setSchemaVersion(fdpConfiguration.getSchemaVersion());
            //fdpRequirementEventPayload.setEventTime();
            fdpRequirementEventPayload.setData(getRequirementEventData(requirementId.toString(),changeMap));
            fdpRequirementEventPayloadList.add(fdpRequirementEventPayload);
        });
        return fdpRequirementEventPayloadList;
    }

    private FdpRequirementEventData getRequirementEventData(String requirementId, ChangeMap changeMap){
        FdpRequirementEventData fdpRequirementEventData = new FdpRequirementEventData();
        fdpRequirementEventData.setRequirementId(requirementId);
        fdpRequirementEventData.setAttribute(changeMap.getAttribute());
        fdpRequirementEventData.setOldValue(changeMap.getOldValue());
        fdpRequirementEventData.setNewValue(changeMap.getNewValue());
        fdpRequirementEventData.setReason(changeMap.getReason());
        fdpRequirementEventData.setUser(changeMap.getUser());
        fdpRequirementEventData.setEventType(changeMap.getEventType());
        return fdpRequirementEventData;
    }
}
