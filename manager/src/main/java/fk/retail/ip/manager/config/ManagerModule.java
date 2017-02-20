package fk.retail.ip.manager.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import fk.retail.ip.zulu.config.ZuluConfiguration;
import fk.sp.common.extensions.dropwizard.db.HasDataSourceFactory;

public class ManagerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HasDataSourceFactory.class).to(ManagerConfiguration.class);
    }

    @Provides
    public ZuluConfiguration getZuluConfiguration(ManagerConfiguration managerConfiguration) {
        return managerConfiguration.getZuluConfiguration();
    }

}
