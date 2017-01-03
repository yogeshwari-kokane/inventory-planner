package fk.sp.inventory-planner.core.config;

import com.google.inject.AbstractModule;

import fk.sp.inventory-planner.core.ClientResource;
import fk.sp.inventory-planner.core.v1.ClientResourceV1;

public class Inventory-plannerModule extends AbstractModule {

  @Override
  protected void configure() {
    //IMP to bind otherwise @Transactional will not work
    bind(ClientResource.class);
    bind(ClientResourceV1.class);
  }
}
