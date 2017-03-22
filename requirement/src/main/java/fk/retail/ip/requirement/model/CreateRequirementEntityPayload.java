package fk.retail.ip.requirement.model;

import fk.retail.ip.bigfoot.model.RequirementEntityData;
import fk.retail.ip.bigfoot.model.RequirementEntityPayload;
import fk.retail.ip.requirement.internal.entities.Requirement;
import org.joda.time.DateTime;

/**
 * Created by yogeshwari.k on 16/03/17.
 */
public class CreateRequirementEntityPayload implements RequirementEntityMapper {

    @Override
    public RequirementEntityPayload convertRequirementToEntityPayload(String requirementId, Requirement requirement) {
        RequirementEntityPayload requirementEntityPayload = new RequirementEntityPayload();
        requirementEntityPayload.setEntityId(getRequirementId(requirement));
        requirementEntityPayload.setData(getRequirementEntityData(requirement));
        //requirementEntityPayload.setSchemaVersion();
        requirementEntityPayload.setUpdatedAt(requirement.getUpdatedAt());
        return requirementEntityPayload;
    }

    private String getRequirementId(Requirement requirement) {
        String requirementId = requirement.getFsn()+requirement.getWarehouse()+(requirement.getCreatedAt().toString());
        return requirementId;
    }

    private RequirementEntityData getRequirementEntityData(Requirement requirement) {
        RequirementEntityData requirementEntityData = new RequirementEntityData();
        String partyId = "FKI";
        DateTime requiredBydate = DateTime.now().plusDays(requirement.getSla());
        requirementEntityData.setRequirementId(getRequirementId(requirement));
        requirementEntityData.setPartyId(partyId);
        requirementEntityData.setFsn(requirement.getFsn());
        requirementEntityData.setWarehouse(requirement.getWarehouse());
        requirementEntityData.setForecast(requirement.getRequirementSnapshot().getForecast());
      //  requirementEntityData.setQuantity(requirement.getQuantity());
        requirementEntityData.setSupplier(requirement.getSupplier());
        requirementEntityData.setApp(requirement.getApp());
        requirementEntityData.setMrp(requirement.getMrp());
        requirementEntityData.setCurrency(requirement.getCurrency());
        requirementEntityData.setRequiredByDate(requiredBydate);
        requirementEntityData.setInventoryQty(requirement.getRequirementSnapshot().getInventoryQty());
        requirementEntityData.setPendingPOQty(requirement.getRequirementSnapshot().getPendingPoQty());
        requirementEntityData.setOpenReqQty(requirement.getRequirementSnapshot().getOpenReqQty());
        requirementEntityData.setIwitIntransitQty(requirement.getRequirementSnapshot().getIwitIntransitQty());
        requirementEntityData.setState(requirement.getState());
        requirementEntityData.setPoId(null);
      //  requirementEntityData.setEnabled(requirement.getEnabled());
        requirementEntityData.setCreatedAt(requirement.getCreatedAt());
        requirementEntityData.setUpdatedAt(requirement.getUpdatedAt());
        requirementEntityData.setPolicyIds(null);
        return requirementEntityData;
    }
}
