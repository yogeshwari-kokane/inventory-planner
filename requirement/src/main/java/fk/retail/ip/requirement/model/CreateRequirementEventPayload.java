package fk.retail.ip.requirement.model;

import com.google.common.collect.Lists;
import fk.retail.ip.bigfoot.model.RequirementEventPayload;
import fk.retail.ip.requirement.model.ChangeMap;
import java.util.List;
/**
 * Created by yogeshwari.k on 17/03/17.
 */
public class CreateRequirementEventPayload implements RequirementEventMapper{

    @Override
    public List<RequirementEventPayload> convertRequirementToEventPayload(String requirementId, List<ChangeMap> changeMaps) {
        List<RequirementEventPayload> requirementEventPayloadList = Lists.newArrayList();
        changeMaps.forEach(changeMap -> {
            RequirementEventPayload requirementEventPayload=new RequirementEventPayload();
            requirementEventPayload.setRequirementId(requirementId);
            requirementEventPayload.setAttribute(changeMap.getAttribute());
            requirementEventPayload.setOldValue(changeMap.getOldValue());
            requirementEventPayload.setNewValue(changeMap.getNewValue());
            requirementEventPayload.setReason(changeMap.getReason());
            requirementEventPayload.setEventType(changeMap.getEventType());
            requirementEventPayload.setUser(changeMap.getUser());
        });
        return requirementEventPayloadList;
    }
}
