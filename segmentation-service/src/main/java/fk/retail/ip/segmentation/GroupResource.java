package fk.retail.ip.segmentation;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import fk.retail.ip.core.Constants;
import fk.retail.ip.segmentation.model.GroupFsnRequest;
import fk.retail.ip.segmentation.model.GroupSegmentationRequest;
import fk.retail.ip.segmentation.service.GroupService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nidhigupta.m on 24/04/17.
 */

@Transactional
@Path("/v1/group")
@Slf4j
public class GroupResource {

    private final GroupService groupService;

    @Inject
    public GroupResource(GroupService groupService) {
        this.groupService = groupService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroups() {
        return Response.ok().entity(groupService.getGroups()).build();
    }


    @GET
    @Path("/staticGroup")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStaticGroups() {
        return Response.ok().entity(groupService.getStaticGroups()).build();
    }


    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupFsns(@PathParam("name") String groupName) {
        List<String> fsns = groupService.getGroupInfo(groupName);
        return Response.ok().entity(fsns).build();
    }

    @GET
    @Path("/download/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupFsnsDownloaded(@PathParam("name") String groupName) throws UnsupportedEncodingException {
        StreamingOutput stream = groupService.getGroupInfoDownload(groupName);
        log.info("Download Request for group " + groupName);
        String fileName = URLEncoder.encode(groupName + ".xlsx", "UTF-8");
        return Response.ok(stream)
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = " + fileName)
                .build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response segmentFsnsToGroup(GroupSegmentationRequest groupSegmentationRequest) {
        try {
            groupService.segmentFsnsToGroup(groupSegmentationRequest);
            return Response.ok().build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Constants.INVALID_GROUP_ID).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGroup(GroupFsnRequest groupFsnRequest) {
        try {
            long groupId = groupService.createGroup(groupFsnRequest);
            return Response.status(200).entity(groupId).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateGroup(GroupFsnRequest groupFsnRequest) {
        try {
            groupService.updateGroup(groupFsnRequest);
            return Response.ok().build();
        } catch (NoResultException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Constants.INVALID_GROUP_ID).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

    }
