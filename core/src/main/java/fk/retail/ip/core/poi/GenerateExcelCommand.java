package fk.retail.ip.core.poi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 * Created by nidhigupta.m on 16/02/17.
 */
@Slf4j
public class GenerateExcelCommand<T> {

    public StreamingOutput generateExcel(List<T> downloadLineItems, String templateName) {
        log.info("Generating excel for {} number of downloadLineItems",downloadLineItems.size());
        SpreadSheetWriter spreadsheet = new SpreadSheetWriter();
        ObjectMapper mapper = new ObjectMapper();
        InputStream template = getClass().getResourceAsStream(templateName);
        StreamingOutput output = (OutputStream out) -> {
            try {
                spreadsheet.populateTemplate(template, out, mapper.convertValue(downloadLineItems, new TypeReference<List<Map>>() {
                }));
            } catch (InvalidFormatException e) {
                throw new WebApplicationException(e);
            }
        };
        return output;
    }
}
