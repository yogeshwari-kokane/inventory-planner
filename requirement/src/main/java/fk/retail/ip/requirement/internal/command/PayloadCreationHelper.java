package fk.retail.ip.requirement.internal.command;

import fk.retail.ip.requirement.model.RequirementChangeMap;

import java.util.UUID;

/**
 * Created by yogeshwari.k on 24/03/17.
 */
public class PayloadCreationHelper {

    public static RequirementChangeMap createChangeMap(String attribute, String oldValue, String newValue, String eventType,
                                                       String reason, String user, String requirementId){
        RequirementChangeMap requirementChangeMap = new RequirementChangeMap();
        requirementChangeMap.setAttribute(attribute);
        requirementChangeMap.setOldValue(oldValue);
        requirementChangeMap.setNewValue(newValue);
        requirementChangeMap.setEventType(eventType);
        requirementChangeMap.setReason(reason);
        requirementChangeMap.setUser(user);
        requirementChangeMap.setEventId(getEventId(requirementId));
        return requirementChangeMap;
    }

    private static String getEventId(String requirementId) {
        return requirementId+ UUID.randomUUID();
    }

}
