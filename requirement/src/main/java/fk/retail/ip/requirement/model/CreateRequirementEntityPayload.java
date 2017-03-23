package fk.retail.ip.requirement.model;

import fk.retail.ip.fdp.config.FdpConfiguration;
import fk.retail.ip.fdp.model.FdpRequirementEntityData;
import fk.retail.ip.fdp.model.FdpRequirementEntityPayload;
import fk.retail.ip.requirement.internal.entities.Requirement;
import org.joda.time.DateTime;

/**
 * Created by yogeshwari.k on 16/03/17.
 */
public class CreateRequirementEntityPayload implements RequirementEntityMapper {

    @Override
    public FdpRequirementEntityPayload convertRequirementToEntityPayload(String requirementId, Requirement requirement) {
        FdpConfiguration fdpConfiguration = new FdpConfiguration();
        FdpRequirementEntityPayload fdpRequirementEntityPayload = new FdpRequirementEntityPayload();
        fdpRequirementEntityPayload.setEntityId(requirementId);
        fdpRequirementEntityPayload.setData(getRequirementEntityData(requirementId,requirement));
        fdpRequirementEntityPayload.setSchemaVersion(fdpConfiguration.getSchema_version());
        fdpRequirementEntityPayload.setUpdatedAt(requirement.getUpdatedAt());
        return fdpRequirementEntityPayload;
    }

    private FdpRequirementEntityData getRequirementEntityData(String requirementId, Requirement requirement) {
        FdpRequirementEntityData fdpRequirementEntityData = new FdpRequirementEntityData();
        String partyId = "FKI";
        DateTime requiredBydate = DateTime.now().plusDays(requirement.getSla());
        fdpRequirementEntityData.setRequirementId(requirementId);
        fdpRequirementEntityData.setPartyId(partyId);
        fdpRequirementEntityData.setFsn(requirement.getFsn());
        fdpRequirementEntityData.setWarehouse(requirement.getWarehouse());
        fdpRequirementEntityData.setForecast(requirement.getRequirementSnapshot().getForecast());
        fdpRequirementEntityData.setQuantity(requirement.getQuantity());
        fdpRequirementEntityData.setSupplier(requirement.getSupplier());
        fdpRequirementEntityData.setApp(requirement.getApp());
        fdpRequirementEntityData.setMrp(requirement.getMrp());
        fdpRequirementEntityData.setCurrency(requirement.getCurrency());
        fdpRequirementEntityData.setRequiredByDate(requiredBydate);
        fdpRequirementEntityData.setInventoryQty(requirement.getRequirementSnapshot().getInventoryQty());
        fdpRequirementEntityData.setPendingPOQty(requirement.getRequirementSnapshot().getPendingPoQty());
        fdpRequirementEntityData.setOpenReqQty(requirement.getRequirementSnapshot().getOpenReqQty());
        fdpRequirementEntityData.setIwitIntransitQty(requirement.getRequirementSnapshot().getIwitIntransitQty());
        fdpRequirementEntityData.setState(requirement.getState());
        fdpRequirementEntityData.setPoId(null);
        fdpRequirementEntityData.setEnabled(requirement.isEnabled());
        fdpRequirementEntityData.setCreatedAt(requirement.getCreatedAt());
        fdpRequirementEntityData.setUpdatedAt(requirement.getUpdatedAt());
        fdpRequirementEntityData.setPolicyIds(null);
        return fdpRequirementEntityData;
    }
}
