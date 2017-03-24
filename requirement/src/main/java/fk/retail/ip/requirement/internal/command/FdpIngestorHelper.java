package fk.retail.ip.requirement.internal.command;

import fk.retail.ip.fdp.model.BatchFdpEventEntityPayload;
import fk.retail.ip.requirement.model.ChangeMap;

/**
 * Created by yogeshwari.k on 24/03/17.
 */
public abstract class FdpIngestorHelper<T> {
    public abstract BatchFdpEventEntityPayload pushToFdp(T requests);

    public ChangeMap createChangeMap(String attribute, String oldValue, String newValue, String eventType, String reason, String user){
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
