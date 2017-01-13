package fk.retail.ip.projection.internal.command;

import java.util.Map;

import fk.retail.ip.projection.internal.enums.ProjectionUploadHeader;
import fk.retail.ip.projection.internal.repository.OverrideEventRepository;

/**
 * Created by nidhigupta.m on 08/01/17.
 */
public class OverrideVerifiedProjectionCommand extends OverrideProjectionCommand {

    public OverrideVerifiedProjectionCommand(OverrideEventRepository overrideEventRepository) {
        super(overrideEventRepository);
    }

    @Override
    public void execute() {
        overrideQuantity(overrideRow);

    }


    private void overrideQuantity(Map<String, Object> overrideRow) {
        String oldQTY = overrideRow.get(ProjectionUploadHeader.quantity.getDisplayName()).toString();
        String newQTY = overrideRow.get(ProjectionUploadHeader.ipc_qty.getDisplayName()).toString();
        String qtyOverrideReason = overrideRow.get(ProjectionUploadHeader.ipc_qty_override_reason.getDisplayName()).toString();
        if(isFieldOverridden(oldQTY, newQTY)) {
            String fieldName = ProjectionUploadHeader.quantity.getDisplayName();
            logProjectionOverride(projectionId, fsn, warehouse, fieldName, oldQTY, newQTY, qtyOverrideReason);
        }
    }
}
