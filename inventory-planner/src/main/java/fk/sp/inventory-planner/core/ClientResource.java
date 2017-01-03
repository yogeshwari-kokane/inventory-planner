package fk.sp.inventory-planner.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;

import java.net.URI;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import fk.sp.inventory-planner.core.internal.entities.Client;
import fk.sp.inventory-planner.core.internal.services.ClientService;
import fk.sp.inventory-planner.core.internal.version.Versions;
import fk.sp.inventory-planner.core.models.ClientsResponse;

/**
 * @resource client
 */
@Path(Versions.V2_PLUS_PATH + "client")
@Singleton
@Transactional
public class ClientResource {

  private final ClientService clientService;

  @Inject
  public ClientResource(ClientService clientService) {
    this.clientService = clientService;
  }

  /**
   * To fetch All Clients
   *
   * @return
   */
  @GET
  @Timed
  @ExceptionMetered
  @Produces(MediaType.APPLICATION_JSON)
  public ClientsResponse getClients(
      @PathParam("v") @DefaultValue(Versions.LATEST) String version,
      @QueryParam("page_no") @DefaultValue("1") int pageNo,
      @QueryParam("page_size") @DefaultValue("10") int pageSize
  ) {

    return clientService.getClientsPaginated(pageNo, pageSize);
  }

  /**
   * Used to fetch a client details
   *
   * @responseType fk.sp.inventory-planner.core.internal.entities.Client
   * @responseMessage 200 Client Exists `fk.sp.inventory-planner.core.internal.entities.Client
   * @responseMessage 404 Client Not found
   */
  @Path("/{name}")
  @GET
  @Timed
  @ExceptionMetered
  @Produces(MediaType.APPLICATION_JSON)
  public Response getClient(
      @PathParam("name") String clientName,
      @PathParam("v") @DefaultValue(Versions.LATEST) String version) {
    Optional<Client> client = clientService.getClient(clientName);
    if (client.isPresent()) {
      return Response.ok(client.get()).build();
    } else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }


  /**
   * Use to create a new Client
   *
   * @responseMessage 201 created
   */
  @PUT
  @Timed
  @ExceptionMetered
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createClient(
      Client client,
      @PathParam("v") @DefaultValue(Versions.LATEST) String version,
      @Context UriInfo uriInfo) {

    client = clientService.createClient(client);
    URI clientUri = uriInfo.getBaseUriBuilder().path(ClientResource.class)
        .path(ClientResource.class, "getClient")
        .build(client.getName());
    return Response.created(clientUri).build();
  }


}
