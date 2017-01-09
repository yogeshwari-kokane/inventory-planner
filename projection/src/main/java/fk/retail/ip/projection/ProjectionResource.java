package fk.retail.ip.projection;

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import fk.retail.ip.projection.internal.command.UploadProjectionCommand;
import fk.retail.ip.projection.internal.exception.ProjectionOverrideException;

/**
 * Created by nidhigupta.m on 06/01/17.
 */

@Path("/projection")
public class ProjectionResource {

    private Provider<UploadProjectionCommand> overrideProjectionCommandProvider;

    @Inject
    public ProjectionResource(Provider<UploadProjectionCommand> overrideProjectionCommandProvider) {
        this.overrideProjectionCommandProvider = overrideProjectionCommandProvider;
    }

    @GET
    public Response sayHello() {
        String name = "Nidhi";
        return Response.ok(name).build();
    }


    @POST
    @Path("/upload")
    public Response uploadProjectionOverride(@FormDataParam("file")InputStream inputStream,
                                             @FormDataParam("file")FormDataContentDisposition fileDetails,
                                             Map<String, Object> params) throws IOException, InvalidFormatException, ProjectionOverrideException {

        overrideProjectionCommandProvider.get().uploadProjectionOverride(inputStream, params);
        return Response.ok().build();

    }


}
