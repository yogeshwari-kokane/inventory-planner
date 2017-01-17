package fk.retail.ip.projection.internal.command;

import com.google.common.collect.Maps;
import fk.retail.ip.projection.internal.enums.ProjectionUploadHeader;
import fk.retail.ip.projection.internal.exception.ProjectionOverrideException;
import java.util.Map;

/**
 * Created by nidhigupta.m on 08/01/17.
 */
public abstract class OverrideProjectionCommand {

    private Map<String, Object> overrideRow = Maps.newHashMap();
    private Map<Long, String> errorMap = Maps.newHashMap();

    public OverrideProjectionCommand withOverrideRow(Map<String, Object> overrideRow) {
        this.overrideRow = overrideRow;
        return this;
    }

    public void execute() throws ProjectionOverrideException {
        isValidRow();

    }

    public boolean isValidRow() throws ProjectionOverrideException {
        if (overrideRow.get(ProjectionUploadHeader.projection_id.getDisplayName()) == null
                || overrideRow.get(ProjectionUploadHeader.projection_item_id.getDisplayName()) == null
                || overrideRow.get(ProjectionUploadHeader.fsn.getDisplayName()) == null
                || overrideRow.get(ProjectionUploadHeader.warehouse.getDisplayName()) == null) {
            errorMap.put(Long.parseLong(overrideRow.get(ProjectionUploadHeader.projection_item_id.getDisplayName()).toString()), "id1,fsn and/or warehouse missing");
            throw new ProjectionOverrideException("Projection ids,fsn and/or warehouse missing");
        }
        return true;
    }

    public abstract void executeStateOverride();

}
