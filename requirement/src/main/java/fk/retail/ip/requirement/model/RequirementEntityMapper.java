package fk.retail.ip.requirement.model;

import fk.retail.ip.bigfoot.model.RequirementEntityPayload;
import fk.retail.ip.requirement.internal.entities.Requirement;

/**
 * Created by yogeshwari.k on 16/03/17.
 */
public interface RequirementEntityMapper {
    RequirementEntityPayload convertRequirementToEntityPayload(String requirementId, Requirement requirement);
}
