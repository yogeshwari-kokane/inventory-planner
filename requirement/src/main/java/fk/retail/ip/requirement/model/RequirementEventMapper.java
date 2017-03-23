package fk.retail.ip.requirement.model;

import fk.retail.ip.bigfoot.model.RequirementEventPayload;
import fk.retail.ip.requirement.model.ChangeMap;
import java.util.List;
/**
 * Created by yogeshwari.k on 17/03/17.
 */
public interface RequirementEventMapper {
    List<RequirementEventPayload> convertRequirementToEventPayload(String RequirementId, List<ChangeMap> changeMaps);
}