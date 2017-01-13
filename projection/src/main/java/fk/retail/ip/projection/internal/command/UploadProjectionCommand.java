package fk.retail.ip.projection.internal.command;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fk.retail.ip.core.poi.SpreadSheetReader;
import fk.retail.ip.projection.internal.entities.ProjectionItem;
import fk.retail.ip.projection.internal.enums.ProjectionUploadHeader;
import fk.retail.ip.projection.internal.exception.ProjectionOverrideException;
import fk.retail.ip.projection.internal.factory.ProjectionOverrideFactory;
import fk.retail.ip.projection.internal.repository.ProjectionItemRepository;

/**
 * Created by nidhigupta.m on 06/01/17.
 */
public class UploadProjectionCommand {

    private final SpreadSheetReader spreadSheetReader;
    private final ProjectionItemRepository projectionItemRepository;
    private final ProjectionOverrideFactory projectionOverrideFactory;

    @Inject
    public UploadProjectionCommand(SpreadSheetReader spreadSheetReader, ProjectionItemRepository projectionItemRepository, ProjectionOverrideFactory projectionOverrideFactory) {
        this.spreadSheetReader = spreadSheetReader;
        this.projectionItemRepository = projectionItemRepository;
        this.projectionOverrideFactory = projectionOverrideFactory;
    }

    public void uploadProjectionOverride(InputStream inputStream, Map<String, Object> params) throws IOException, InvalidFormatException, ProjectionOverrideException {

        List<Map<String, Object>> projectionOverrideRows = spreadSheetReader.writeToCsv(inputStream);
        Map<Long, Map<String, Object>> mappedProjectionOverrideRows = mapUploadedRowsToProjectionId(projectionOverrideRows);
        List<Long> projectionItemIds = new ArrayList<>(mappedProjectionOverrideRows.keySet());
        List<ProjectionItem> projectionItems = projectionItemRepository.getProjectionByIds(projectionItemIds);
        projectionItems.stream()
                .filter()
                .forEach(pi -> {
            try {
                pi.overrideProjection(projectionOverrideFactory, mappedProjectionOverrideRows.get(pi.getId()));
            } catch (ProjectionOverrideException e) {
                e.printStackTrace();
            }
        });

    }



    private Map<Long, Map<String, Object>> mapUploadedRowsToProjectionId(List<Map<String, Object>> projectionOverrideRows) {
        Map<Long, Map<String, Object>> mappedProjectionOverrideRows = Maps.newHashMap();
        for (Map<String, Object> projectionOverrideRow : projectionOverrideRows) {
            Long projectionItemId = Long.parseLong(projectionOverrideRow.get(ProjectionUploadHeader.projection_item_id.getDisplayName()).toString());
            mappedProjectionOverrideRows.put(projectionItemId,projectionOverrideRow);
        }
        return mappedProjectionOverrideRows;
    }

}
