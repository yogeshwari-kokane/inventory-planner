package fk.retail.ip.requirement.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import fk.retail.ip.requirement.model.CalculateRequirementRequest;
import fk.retail.ip.requirement.model.DownloadRequirementRequest;
import fk.retail.ip.requirement.model.RequirementApprovalRequest;
import fk.retail.ip.requirement.model.UploadResponse;
import fk.retail.ip.requirement.service.RequirementService;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONException;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@Transactional
@Path("/v1/requirement")
@Slf4j
public class RequirementResource {

    private final RequirementService requirementService;

    @Inject
    public RequirementResource(RequirementService requirementService) {
        this.requirementService = requirementService;
    }

    @POST
    public void calculateRequirement(@Valid CalculateRequirementRequest calculateRequirementRequest) {
        requirementService.calculateRequirement(calculateRequirementRequest);
    }

    @POST
    @Path("/download")
    @Timed
    public Response download(DownloadRequirementRequest downloadRequirementRequest) {

        log.info("Download Requirement request received " + downloadRequirementRequest);
        StreamingOutput stream = requirementService.downloadRequirement(downloadRequirementRequest);
        return Response.ok(stream)
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = projection.xlsx")
                    .build();

    }

    @POST
    @Path("/upload")
    @Timed
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadProjectionOverride(
            @FormDataParam("datafile") InputStream inputStream,
            @FormDataParam("state") String state
    ) {

//        if (inputStream.available() > 0) {
//            System.out.println("stream is present");
//        } else{
//            return  " {\"status\" : \"success\"}";
//        }
//        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//        StringBuilder stringBuilder = new StringBuilder();
//        String line;
//        while((line = br.readLine()) != null) {
//            stringBuilder.append(line);
//        }
//        System.out.println(stringBuilder.toString());

//        byte[] buffer = new byte[1024];
//        int bytesRead;
//        OutputStream outputStream = new FileOutputStream(file);
//        do {
//             bytesRead = inputStream.read(buffer);
//                outputStream.write(buffer, 0, bytesRead);
//        } while(bytesRead == 1024);

//        inputStream.read(buffer);

        //outputStream.write(buffer);
        log.info("Upload Requirement request received for " + state + " state");
        try {
            UploadResponse uploadResponse = requirementService.uploadRequirement(inputStream, state);
            log.info("Successfully updated " + uploadResponse.getSuccessfulRowCount() + " records");
            return Response.ok(uploadResponse).build();
        } catch (IOException ioException) {
            log.info("IO exception occurred");
            return Response.status(400).build();
        } catch (InvalidFormatException invalidFormat) {
            log.info("Invalid format exception");
            return Response.status(400).build();
        }
    }

    @PUT
    @Path("/state")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public String changeState(RequirementApprovalRequest request) throws JSONException {
        return requirementService.changeState(request);
    }
}
