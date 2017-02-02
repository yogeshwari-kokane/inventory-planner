package fk.retail.ip.manager.config;


import com.google.inject.AbstractModule;

import fk.sp.common.extensions.dropwizard.db.HasDataSourceFactory;

public class ManagerModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(HasDataSourceFactory.class).to(ManagerConfiguration.class);
  }


}
