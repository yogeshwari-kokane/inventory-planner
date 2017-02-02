package fk.retail.ip.requirement.config;

import com.google.inject.AbstractModule;
import fk.retail.ip.requirement.resource.RequirementResource;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class RequirementModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(RequirementResource.class);
    }
}
