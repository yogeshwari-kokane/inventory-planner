package fk.retail.ip.segmentation.config;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import java.util.function.Predicate;

import fk.retail.ip.core.Constants;
import fk.retail.ip.core.entities.IPGroup;
import fk.retail.ip.core.repository.GroupFsnRepository;
import fk.retail.ip.core.repository.GroupRepository;
import fk.retail.ip.core.repository.JPAGroupFsnRepository;
import fk.retail.ip.core.repository.JPAGroupRepository;
import fk.retail.ip.core.repository.JPAProductDataRepository;
import fk.retail.ip.core.repository.ProductDataRepository;
import fk.retail.ip.segmentation.GroupResource;
import fk.retail.ip.segmentation.job.GroupSegmentationAlertJob;
import fk.retail.ip.segmentation.predicate.IsGroupSegmentedToday;

/**
 * Created by nidhigupta.m on 26/04/17.
 */
public class SegmentationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GroupResource.class);
        bind(GroupSegmentationAlertJob.class);
        bind(GroupFsnRepository.class).to(JPAGroupFsnRepository.class);
        bind(GroupRepository.class).to(JPAGroupRepository.class);
        bind(ProductDataRepository.class).to(JPAProductDataRepository.class);
        bind(new TypeLiteral<Predicate<IPGroup>>(){})
                .annotatedWith(Names.named(Constants.IS_GROUP_SEGMENTED_TODAY))
                .to(IsGroupSegmentedToday.class);

    }

}
