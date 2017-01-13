package fk.retail.ip.projection.internal.command;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import org.apache.commons.lang.StringUtils;

import java.util.Map;

import fk.retail.ip.projection.internal.enums.ProjectionUploadHeader;
import fk.retail.ip.projection.internal.exception.ProjectionOverrideException;
import fk.retail.ip.projection.internal.repository.OverrideEventRepository;

/**
 * Created by nidhigupta.m on 08/01/17.
 */
public abstract class OverrideProjectionCommand {

    protected Map<String, Object> overrideRow = Maps.newHashMap();
    protected Map<Long, String> errorMap = Maps.newHashMap();
    protected String overrideEntityType = "Projection";
    protected String overrideType = "Absolute";
    protected OverrideEventRepository overrideEventRepository;
    protected final String fsn = overrideRow.get(ProjectionUploadHeader.fsn.getDisplayName()).toString();
    protected final String warehouse = overrideRow.get(ProjectionUploadHeader.warehouse.getDisplayName()).toString();
    protected final Long projectionId = Long.parseLong(overrideRow.get(ProjectionUploadHeader.projection_id.getDisplayName()).toString());
    protected final Long projectionItemId = Long.parseLong(overrideRow.get(ProjectionUploadHeader.projection_item_id.getDisplayName()).toString());


    @Inject
    public OverrideProjectionCommand(OverrideEventRepository overrideEventRepository) {
        this.overrideEventRepository = overrideEventRepository;
    }

    public OverrideProjectionCommand withOverrideRow(Map<String, Object> overrideRow) {
        this.overrideRow = overrideRow;
        return this;
    }
//
//    public void execute() throws ProjectionOverrideException {
//        isValidRow();
//    }

    public boolean isValidRow() throws ProjectionOverrideException {
        if (overrideRow.get(ProjectionUploadHeader.fsn.getDisplayName()) == null ||
                overrideRow.get(ProjectionUploadHeader.warehouse.getDisplayName()) == null) {
            errorMap.put(Long.parseLong(overrideRow.get(ProjectionUploadHeader.projection_item_id.getDisplayName()).toString()), "Fsn and/or warehouse missing");
            throw new ProjectionOverrideException("Fsn and/or warehouse missing");
        }
        return true;
    }

    protected boolean isFieldOverridden(Object oldField, Object newField) {
        String oldFieldString = oldField.toString();
        String newFieldString = newField.toString();
        return (!oldFieldString.equals(newFieldString) && StringUtils.isNotBlank(newFieldString));
    }



    protected void logProjectionOverride(Long entityId, String fsn, String warehouse, String fieldName, String oldValue, String newValue, String overrideReason) {
        overrideEventRepository.persistOverrideEvent(entityId, fsn, warehouse, fieldName, oldValue, newValue, overrideReason, overrideType, overrideEntityType);
    }

    public abstract void execute() throws ProjectionOverrideException;

}
