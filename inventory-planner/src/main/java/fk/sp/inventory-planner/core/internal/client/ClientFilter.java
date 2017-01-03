package fk.sp.inventory-planner.core.internal.client;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;

import fk.sp.common.extensions.RequestContext;
import fk.sp.common.extensions.exception.BaseException;
import fk.sp.common.extensions.exception.mapper.BaseExceptionMapper;

//PreMatching ensures that exceptions inside filter are not attributed to ExceptionMetred at resource methods
@PreMatching
public class ClientFilter implements ContainerRequestFilter {

  private final ClientCache clientCache;

  @Context
  private BaseExceptionMapper baseExceptionMapper;

  @Inject
  public ClientFilter(ClientCache clientCache) {
    this.clientCache = clientCache;
  }

  @Override
  @Transactional
  public void filter(ContainerRequestContext requestContext) throws IOException {
    String clientId = RequestContext.getClientId();
    if (!clientCache.getClient(clientId).isPresent()) {
      throw new BaseException("UNKNOWN_CLIENT_ID", "Client id '" + clientId + "' not allowed");
    }
  }

}
