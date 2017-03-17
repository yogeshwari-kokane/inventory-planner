package fk.retail.ip.manager.config;

import fk.retail.ip.zulu.config.ZuluConfiguration;
import fk.retail.ip.bigfoot.config.BigfootConfiguration;
import fk.sp.common.extensions.dropwizard.db.HasDataSourceFactory;
import flipkart.retail.server.admin.config.RotationManagementConfig;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class ManagerConfiguration extends Configuration implements HasDataSourceFactory {

    private DataSourceFactory dataSource;

    @Valid
    @NotNull
    private RotationManagementConfig rotationManagementConfig;

    @NotNull
    private ZuluConfiguration zuluConfiguration;

    @Null
    private BigfootConfiguration bigfootConfiguration;

    @Valid
    @NotNull
    private JerseyClientConfiguration clientConfiguration;

    @Override
    public DataSourceFactory getDatabaseConfiguration() {
        return dataSource;
    }
}
