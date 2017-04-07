package fk.retail.ip.requirement.internal.command.download;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fk.retail.ip.core.poi.SpreadSheetWriter;
import fk.retail.ip.requirement.internal.poi.RequirementSpreadSheetWriter;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;

/**
 * Created by nidhigupta.m on 16/02/17.
 */
@Slf4j
public class GenerateExcelCommand {

    public StreamingOutput generateExcel(List<RequirementDownloadLineItem> requirementDownloadLineItems, String templateName) {
        log.info("Generating excel for {} number of requirements",requirementDownloadLineItems.size());
        SpreadSheetWriter spreadsheet = new SpreadSheetWriter();
        ObjectMapper mapper = new ObjectMapper();
        InputStream template = getClass().getResourceAsStream(templateName);
        StreamingOutput output = (OutputStream out) -> {
            try {
                spreadsheet.populateTemplate(template, out, mapper.convertValue(requirementDownloadLineItems, new TypeReference<List<Map>>() {
                }));
            } catch (InvalidFormatException e) {
                throw new WebApplicationException(e);
            }
        };
        return output;
    }
}
