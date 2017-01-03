package fk.sp.inventory-planner.core.internal.client;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.io.PrintWriter;
import java.util.stream.Collectors;

import io.dropwizard.servlets.tasks.Task;

public class RefreshClientTask extends Task {

  private final ClientCache clientCache;

  @Inject
  public RefreshClientTask(ClientCache clientCache) {
    super("reload_client_cache");
    this.clientCache = clientCache;
  }

  @Override
  @Transactional
  public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output)
      throws Exception {
    output.write("Reloading cache......\n\n");
    String cachedClients =
        this.clientCache.populateCache().stream().collect(Collectors.joining("\n"));
    output.write("Cached clients are:\n" + cachedClients);
  }
}
