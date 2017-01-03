package fk.sp.inventory-planner.core.v1;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import fk.sp.inventory-planner.core.ClientResource;
import fk.sp.inventory-planner.core.internal.entities.Client;
import fk.sp.inventory-planner.core.internal.services.ClientService;
import fk.sp.inventory-planner.core.internal.version.Versions;

/**
 * @resource clientV1
 */
@Path(Versions.V1_PATH + "client")
@Singleton
@Transactional
public class ClientResourceV1 {

  private final ClientService clientService;
  private final ClientResource clientResource;

  @Inject
  public ClientResourceV1(ClientService clientService, ClientResource clientResource) {
    this.clientService = clientService;
    this.clientResource = clientResource;
  }

  /**
   * To fetch All Clients
   * @description Returning All will not scale hence changed to pagination in next version
   * @return
   */
  @GET
  @Timed
  @ExceptionMetered
  @Produces(MediaType.APPLICATION_JSON)
  public List<Client> getClients(
      @PathParam("v") @DefaultValue(Versions.FIRST) String version) {
    return clientService.getClients();
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
      @PathParam("v") @DefaultValue(Versions.FIRST) String version) {
    return clientResource.getClient(clientName, version);
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
      @PathParam("v") @DefaultValue(Versions.FIRST) String version,
      @Context UriInfo uriInfo) {

    return clientResource.createClient(client, version, uriInfo);
  }


}
