package fk.retail.ip.requirement.model;

import com.google.common.collect.Lists;
import fk.retail.ip.fdp.model.FdpRequirementEventPayload;

import java.util.List;
/**
 * Created by yogeshwari.k on 17/03/17.
 */
public class CreateRequirementEventPayload implements RequirementEventMapper{

    @Override
    public List<FdpRequirementEventPayload> convertRequirementToEventPayload(String requirementId, List<ChangeMap> changeMaps) {
        List<FdpRequirementEventPayload> fdpRequirementEventPayloadList = Lists.newArrayList();
        changeMaps.forEach(changeMap -> {
            FdpRequirementEventPayload fdpRequirementEventPayload =new FdpRequirementEventPayload();
            fdpRequirementEventPayload.setRequirementId(requirementId);
            fdpRequirementEventPayload.setAttribute(changeMap.getAttribute());
            fdpRequirementEventPayload.setOldValue(changeMap.getOldValue());
            fdpRequirementEventPayload.setNewValue(changeMap.getNewValue());
            fdpRequirementEventPayload.setReason(changeMap.getReason());
            fdpRequirementEventPayload.setEventType(changeMap.getEventType());
            fdpRequirementEventPayload.setUser(changeMap.getUser());
            fdpRequirementEventPayloadList.add(fdpRequirementEventPayload);
        });
        return fdpRequirementEventPayloadList;
    }
}
