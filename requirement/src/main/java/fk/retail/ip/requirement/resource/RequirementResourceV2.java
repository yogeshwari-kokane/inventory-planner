package fk.retail.ip.requirement.resource;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import fk.retail.ip.requirement.model.DownloadRequirementRequest2;
import fk.retail.ip.requirement.model.RequirementSearchRequestV2;
import fk.retail.ip.requirement.model.SearchResponseV2;
import fk.retail.ip.requirement.service.RequirementServiceV2;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by yogeshwari.k on 09/05/17.
 */
@Transactional
@Path("/v2/requirement")
@Slf4j
public class RequirementResourceV2 {

    private final RequirementServiceV2 requirementService;

    @Inject
    public RequirementResourceV2(RequirementServiceV2 requirementService) {
        this.requirementService = requirementService;
    }

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResponseV2.GroupedResponse search(RequirementSearchRequestV2 request) throws JSONException {
         return requirementService.searchV2(request);
    }

    @POST
    @Path("/download")
    @Timed(name="downloadTimer")
    @Metered(name="downloadMeter")
    @ExceptionMetered(name="downloadExceptionMeter")
    public Response download(DownloadRequirementRequest2 downloadRequirementRequest) {
        log.info("Download Requirement request received " + downloadRequirementRequest);
        StreamingOutput stream = requirementService.downloadRequirement(downloadRequirementRequest);
        return Response.ok(stream)
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = projection.xlsx")
                .build();

    }

}
