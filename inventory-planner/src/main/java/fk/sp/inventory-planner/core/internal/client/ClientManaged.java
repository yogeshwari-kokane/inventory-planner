package fk.sp.inventory-planner.core.internal.client;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import io.dropwizard.lifecycle.Managed;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientManaged implements Managed {

  private final ClientCache clientCache;

  @Inject
  public ClientManaged(ClientCache clientCache) {
    this.clientCache = clientCache;
  }


  @Override
  @Transactional
  public void start() throws Exception {
    log.info("Populating client cache");
    this.clientCache.populateCache();
  }

  @Override
  public void stop() throws Exception {

  }
}
