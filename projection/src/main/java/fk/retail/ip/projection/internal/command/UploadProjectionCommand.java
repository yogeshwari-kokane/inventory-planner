package fk.retail.ip.projection.internal.command;

import com.google.inject.Inject;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import fk.retail.ip.core.poi.SpreadSheetReader;
import fk.retail.ip.projection.internal.entities.Projection;
import fk.retail.ip.projection.internal.enums.ProjectionUploadHeader;
import fk.retail.ip.projection.internal.exception.ProjectionOverrideException;
import fk.retail.ip.projection.internal.factory.ProjectionOverrideFactory;
import fk.retail.ip.projection.internal.repository.ProjectionRepository;

/**
 * Created by nidhigupta.m on 06/01/17.
 */
public class UploadProjectionCommand {

    private final SpreadSheetReader spreadSheetReader;
    private final ProjectionRepository projectionRepository;
    private final ProjectionOverrideFactory projectionOverrideFactory;

    @Inject
    public UploadProjectionCommand(SpreadSheetReader spreadSheetReader, ProjectionRepository projectionRepository, ProjectionOverrideFactory projectionOverrideFactory) {
        this.spreadSheetReader = spreadSheetReader;
        this.projectionRepository = projectionRepository;
        this.projectionOverrideFactory = projectionOverrideFactory;
    }

    public void uploadProjectionOverride(InputStream inputStream, Map<String, Object> params) throws IOException, InvalidFormatException, ProjectionOverrideException {

        List<Map<String, Object>> projectionOverrideRows = spreadSheetReader.writeToCsv(inputStream);
//        List<Long> projectionIds = projectionOverrideRows.stream().flatMap(
//                         m -> m.entrySet().stream()
//                        .filter(e-> e.getKey().equals("id1"))
//                        .map(e -> Long.parseLong(e.getValue().toString()))
//        ).collect(Collectors.toList());
//        Projection projection = projectionRepository.getProjectionByIds(projectionId);
        for (Map<String, Object> projectionOverrideRow : projectionOverrideRows) {
            Long projectionId = Long.parseLong(projectionOverrideRow.get(ProjectionUploadHeader.projection_id.getDisplayName()).toString());
            Projection projection = projectionRepository.getProjectionById(projectionId);
            projection.overrideProjection(projectionOverrideFactory, projectionOverrideRow);
        }

    }

}
