package fk.retail.ip.requirement.model;

import fk.retail.ip.fdp.model.FdpRequirementEventPayload;

import java.util.List;
/**
 * Created by yogeshwari.k on 17/03/17.
 */
public interface RequirementEventMapper {
    List<FdpRequirementEventPayload> convertRequirementToEventPayload(String RequirementId, List<ChangeMap> changeMaps);
}
