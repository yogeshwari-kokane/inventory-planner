package fk.sp.inventory-planner.core.config;

import com.google.inject.Inject;
import com.google.inject.Provider;

import fk.sp.inventory-planner.core.internal.client.ClientFilter;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class Inventory-plannerBundle implements Bundle{

  private final Provider<ClientFilter> clientFilterProvider;

  @Inject
  public Inventory-plannerBundle(Provider<ClientFilter> clientFilterProvider) {
    this.clientFilterProvider = clientFilterProvider;
  }

  @Override
  public void initialize(Bootstrap<?> bootstrap) {

  }

  @Override
  public void run(Environment environment) {
    environment.jersey().register(clientFilterProvider.get());
  }
}
