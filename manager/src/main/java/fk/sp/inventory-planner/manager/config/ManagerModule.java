package fk.sp.inventory-planner.manager.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import fk.sp.common.extensions.dropwizard.db.HasDataSourceFactory;
import fk.sp.inventory-planner.core.config.Inventory-plannerConfiguration;

public class ManagerModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(HasDataSourceFactory.class).to(ManagerConfiguration.class);
  }

  @Provides
  public Inventory-plannerConfiguration getInventory-plannerConfiguration(ManagerConfiguration managerConfiguration) {
    return managerConfiguration.getInventory-planner();
  }
}
