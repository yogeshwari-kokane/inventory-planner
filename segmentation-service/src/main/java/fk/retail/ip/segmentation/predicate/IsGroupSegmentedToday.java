package fk.retail.ip.segmentation.predicate;

import com.google.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.function.Predicate;

import fk.retail.ip.core.repository.GroupFsnRepository;
import fk.retail.ip.core.entities.IPGroup;

/**
 * Created by nidhigupta.m on 27/04/17.
 */
public class IsGroupSegmentedToday implements Predicate<IPGroup> {

    private final GroupFsnRepository groupFsnRepository;

    @Inject
    public  IsGroupSegmentedToday(GroupFsnRepository groupFsnRepository) {
        this.groupFsnRepository = groupFsnRepository;
    }

    @Override
    public boolean test(IPGroup group) {
        Date date = groupFsnRepository.fetchCreatedAt(group);
        if (date == null) return false;
        return DateUtils.isSameDay(date, new Date());
    }
}
