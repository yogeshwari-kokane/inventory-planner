package fk.sp.inventory-planner.manager.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import fk.sp.common.extensions.dropwizard.db.HasDataSourceFactory;
import fk.sp.inventory-planner.core.config.Inventory-plannerConfiguration;
import flipkart.retail.server.admin.config.RotationManagementConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Data;

@Data
public class ManagerConfiguration extends Configuration implements HasDataSourceFactory{

  private Inventory-plannerConfiguration inventory-planner;

  private DataSourceFactory dataSource;

  @Valid
  @NotNull
  private RotationManagementConfig rotationManagementConfig;

  @Override
  public DataSourceFactory getDatabaseConfiguration() {
    return dataSource;
  }
}
