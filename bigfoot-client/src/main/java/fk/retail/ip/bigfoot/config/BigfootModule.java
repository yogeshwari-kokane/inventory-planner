package fk.retail.ip.bigfoot.config;

import com.google.inject.AbstractModule;
import fk.retail.ip.bigfoot.model.CreateRequirementEntityPayload;
import fk.retail.ip.bigfoot.model.CreateRequirementEventPayload;
import fk.retail.ip.bigfoot.model.RequirementEntityMapper;
import fk.retail.ip.bigfoot.model.RequirementEventMapper;

/**
 * Created by yogeshwari.k on 16/03/17.
 */
public class BigfootModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(RequirementEntityMapper.class).to(CreateRequirementEntityPayload.class);
        bind(RequirementEventMapper.class).to(CreateRequirementEventPayload.class);
    }
}
