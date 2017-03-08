package fk.retail.ip.requirement.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.common.io.FileBackedOutputStream;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import fk.retail.ip.requirement.internal.enums.OverrideKeys;
import fk.retail.ip.requirement.internal.exception.InvalidRequirementStateException;
import fk.retail.ip.requirement.internal.exception.NoRequirementsSelectedException;
import fk.retail.ip.requirement.model.DownloadRequirementRequest;
import fk.retail.ip.requirement.model.RequirementUploadLineItem;
import fk.retail.ip.requirement.model.UploadResponse;
import fk.retail.ip.requirement.service.RequirementService;
import io.dropwizard.hibernate.UnitOfWork;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.*;
import fk.retail.ip.requirement.model.RequirementApprovalRequest;
import fk.retail.ip.requirement.service.RequirementService;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import io.dropwizard.jersey.errors.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONException;

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
    @Path("/download")
    @Timed
    public Response download(DownloadRequirementRequest downloadRequirementRequest) {

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
    public Response uploadProjectionOverride(@FormDataParam("datafile") InputStream inputStream,
                                           @FormDataParam("state") String state)
            throws IOException, InvalidFormatException {

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
        System.out.println(state);
        try {
            //List<RequirementUploadLineItem> result = requirementService.uploadRequirement(new FileInputStream("/Users/agarwal.vaibhav/Desktop/test_proposed.xlsx"), fileDetails, state);
            UploadResponse uploadResponse = requirementService.uploadRequirement(inputStream, state);
            log.info("Successfully uploaded " + uploadResponse.getSuccessfulRowCount() + "records");


            //UploadResponse uploadResponse = new UploadResponse();

            //JSONObject response = new JSONObject();
//            if (result.isEmpty()) {
//                //response.put("status", "success");
//                System.out.println("all were successful");
//                uploadResponse.setRequirementUploadLineItems(result);
//                uploadResponse.setStatus(OverrideKeys.SUCCESS.toString());
//                uploadResponse.setSuccessfulRowCount(0);
//            } else {
//                System.out.println("atleast one failed");
//                uploadResponse.setRequirementUploadLineItems(result);
//                uploadResponse.setStatus(OverrideKeys.FAILURE.toString());
//                JSONObject responseBody = new JSONObject();
//                List<JSONObject> responseList = new ArrayList<>();
//                for(RequirementUploadLineItem row : result) {
//                    responseBody.put("failureReason", row.getFailureReason());
//                    responseBody.put("rowNumber", row.getRowNumber());
//                    responseBody.put("warehouse", row.getWarehouse());
//                    responseBody.put("fsn", row.getFsn());
//                    responseList.add(responseBody);
//                }
//                response.put("status", "failed");
//                response.put("response", responseList);

//                responseBody.put("rowNumber", result.get(0).getRowNumber());
//                responseBody.put("fsn", result.get(0).getFsn());
//                responseBody.put("warehouse", result.get(0).getWarehouse());
//                responseBody.put("failureReason", result.get(0).getFailureReason());
//                response.put("response", responseBody);
//                System.out.println(result.get(0).getFailureReason());
//                System.out.println(result.get(0).getFsn());
//                System.out.println(result.get(0).getRowNumber());
//            }
            return Response.ok(uploadResponse).build();

        } catch (InvalidRequirementStateException invalidStateException) {
            log.info("Invalid Requirement State");
            return Response.status(400).entity(invalidStateException.getMessage()).type("text/plain").build();
        } catch (NoRequirementsSelectedException noRequirement) {
            log.info("No requirement was found for the Uploaded File");
            return Response.status(400).entity(new ErrorMessage("No requirement Found"))
                    .type(MediaType.APPLICATION_JSON).build();
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
