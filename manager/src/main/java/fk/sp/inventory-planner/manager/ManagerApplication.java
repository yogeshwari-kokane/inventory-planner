package fk.sp.inventory-planner.manager;

import com.google.common.collect.Sets;
import com.google.inject.Stage;

import com.hubspot.dropwizard.guice.GuiceBundle;

import java.util.Properties;

import fk.sp.common.extensions.guice.jpa.spring.JpaWithSpringModule;
import fk.sp.inventory-planner.core.config.Inventory-plannerBundle;
import fk.sp.inventory-planner.core.config.Inventory-plannerModule;
import fk.sp.inventory-planner.manager.config.ManagerConfiguration;
import fk.sp.inventory-planner.manager.config.ManagerModule;
import flipkart.retail.server.admin.bundle.RotationManagementBundle;
import flipkart.retail.server.admin.config.RotationManagementConfig;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class ManagerApplication extends Application<ManagerConfiguration> {

  private GuiceBundle<ManagerConfiguration> guiceBundle;

  @Override
  public void initialize(Bootstrap<ManagerConfiguration> bootstrap) {
    super.initialize(bootstrap);

    this.guiceBundle = GuiceBundle.<ManagerConfiguration>newBuilder()
        .setConfigClass(ManagerConfiguration.class)
        .addModule(new ManagerModule())
        .addModule(new Inventory-plannerModule())
        .addModule(new JpaWithSpringModule(
            Sets.newHashSet(
                "fk.sp.inventory-planner"
            ), new Properties()))

        .enableAutoConfig(
            "fk.sp.common.extensions.exception",
            "fk.sp.common.extensions.dropwizard.hystrix",
            "fk.sp.common.extensions.dropwizard.logging",
            "fk.sp.common.extensions.dropwizard.jmx",
            "fk.sp.common.extensions.dropwizard.jpa",
            "fk.sp.common.extensions.filter",
            "fk.sp.inventory-planner"
        )
        .build(Stage.DEVELOPMENT);
    bootstrap.addBundle(guiceBundle);

    bootstrap.addBundle(guiceBundle.getInjector().getInstance(Inventory-plannerBundle.class));

    //For oor and bir task and healthcheck integration
    bootstrap.addBundle(new RotationManagementBundle<ManagerConfiguration>() {
      @Override
      public RotationManagementConfig getRotationManagementConfig(
          ManagerConfiguration managerConfiguration) {
        return managerConfiguration.getRotationManagementConfig();
      }
    });

    //For dbMigrations support
    bootstrap.addBundle(new MigrationsBundle<ManagerConfiguration>() {
      public DataSourceFactory getDataSourceFactory(
          ManagerConfiguration configuration) {
        return configuration.getDatabaseConfiguration();
      }
    });

  }

  @Override
  public void run(ManagerConfiguration configuration, Environment environment) throws Exception {

    //For swagger UI
    AssetsBundle assetsBundle = new AssetsBundle("/apidocs", "/apidocs", "index.html", "/apidocs");
    assetsBundle.run(environment);

  }

  public static void main(String[] args) throws Exception {
    ManagerApplication managerApplication = new ManagerApplication();
    managerApplication.run(args);
  }
}
