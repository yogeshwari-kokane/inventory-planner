package fk.retail.ip.projection.internal.command;

import org.apache.commons.lang.StringUtils;

import java.util.Map;

import fk.retail.ip.projection.internal.enums.ProjectionUploadHeader;
import fk.retail.ip.projection.internal.exception.ProjectionOverrideException;
import fk.retail.ip.projection.internal.repository.OverrideEventRepository;

/**
 * Created by nidhigupta.m on 08/01/17.
 */
public class OverrideApprovedProjectionCommand extends OverrideProjectionCommand {


    public OverrideApprovedProjectionCommand(OverrideEventRepository overrideEventRepository) {
        super(overrideEventRepository);
    }

    public void execute() throws ProjectionOverrideException {
        overrideSupplier(overrideRow);
        overrideAPP(overrideRow);
        overrideSLA(overrideRow);
        overrideQuantity(overrideRow);
    }

    private void overrideSupplier(Map<String, Object> overrideRow) throws ProjectionOverrideException {
        String oldSupplier =  overrideRow.get(ProjectionUploadHeader.supplier.getDisplayName()).toString();
        String newSupplier = overrideRow.get(ProjectionUploadHeader.bd_supplier.getDisplayName()).toString();
        String supplierOverrideReason = overrideRow.get(ProjectionUploadHeader.bd_supplier_override_reason.getDisplayName()).toString();

        if (isSupplierOverrideValidated(oldSupplier,newSupplier, supplierOverrideReason, projectionItemId )) {
            String fieldName = ProjectionUploadHeader.supplier.getDisplayName();
            logProjectionOverride(projectionId, fsn, warehouse, fieldName, oldSupplier, newSupplier, supplierOverrideReason);
        }
    }

    private void overrideAPP(Map<String, Object> overrideRow) throws ProjectionOverrideException {
        String oldApp = overrideRow.get(ProjectionUploadHeader.app.getDisplayName()).toString();
        String newApp = overrideRow.get(ProjectionUploadHeader.bd_app.getDisplayName()).toString();
        String appOverrideReason = overrideRow.get(ProjectionUploadHeader.bd_app_override_reason.getDisplayName()).toString();
        if (isAppOverrideValidated(oldApp, newApp, appOverrideReason, projectionItemId)) {
            String fieldName = ProjectionUploadHeader.app.getDisplayName();
            logProjectionOverride(projectionId, fsn, warehouse, fieldName, oldApp, newApp , appOverrideReason);
        }
    }

    private void overrideSLA(Map<String, Object> overrideRow) {
        String oldSLA = overrideRow.get(ProjectionUploadHeader.sla.getDisplayName()).toString();
        String newSLA = overrideRow.get(ProjectionUploadHeader.new_sla.getDisplayName()).toString();
        if(isFieldOverridden(oldSLA, newSLA)) {
            String fieldName = ProjectionUploadHeader.sla.getDisplayName();
            logProjectionOverride(projectionId, fsn, warehouse, fieldName, oldSLA, newSLA, null);
        }
    }

    private void overrideQuantity(Map<String, Object> overrideRow) throws ProjectionOverrideException {
        String oldQTY = overrideRow.get(ProjectionUploadHeader.quantity.getDisplayName()).toString();
        String newQTY = overrideRow.get(ProjectionUploadHeader.bd_quantity.getDisplayName()).toString();
        String qtyOverrideReason = overrideRow.get(ProjectionUploadHeader.bd_quantity_override_reason.getDisplayName()).toString();
        if(isFieldOverridden(oldQTY, newQTY)) {
            String fieldName = ProjectionUploadHeader.quantity.getDisplayName();
            logProjectionOverride(projectionId, fsn, warehouse, fieldName, oldQTY, newQTY, qtyOverrideReason);
        }
    }

    private boolean isSupplierOverrideValidated(String oldSupplier, String newSupplier, String supplierOverrideReason, long projectionItemId) throws ProjectionOverrideException {
        if (isFieldOverridden(oldSupplier, newSupplier)){
            if (StringUtils.isBlank(supplierOverrideReason)) {
                errorMap.put(projectionItemId, "Supplier override reason should be provided");
                throw new ProjectionOverrideException("Supplier override reason should be provided");
            }
        }
        return true;
    }

    private  boolean isAppOverrideValidated(String oldApp, String newApp, String appOverrideReason, long projectionItemId) throws ProjectionOverrideException {
        if (isFieldOverridden(oldApp, newApp)){
            if (StringUtils.isBlank(appOverrideReason)) {
                errorMap.put(projectionItemId, "APP override reason should be provided");
                throw new ProjectionOverrideException("APP override reason should be provided");
            }
        }
        return true;
    }


}
