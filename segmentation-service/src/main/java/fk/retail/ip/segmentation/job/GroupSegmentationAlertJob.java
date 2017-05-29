package fk.retail.ip.segmentation.job;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import fk.retail.ip.core.Constants;
import fk.retail.ip.core.MailSender;
import fk.retail.ip.core.repository.GroupFsnRepository;
import fk.retail.ip.core.repository.GroupRepository;
import fk.retail.ip.core.entities.IPGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nidhigupta.m on 27/04/17.
 */

@Transactional
@Slf4j
public class GroupSegmentationAlertJob implements Job {
    private final GroupFsnRepository groupFsnRepository;
    private final GroupRepository groupRepository;
    private final Predicate<IPGroup> isGroupSegmentedToday;

    @Inject
    public GroupSegmentationAlertJob(@Named(Constants.IS_GROUP_SEGMENTED_TODAY) Predicate<IPGroup> isGroupSegmentedToday, GroupFsnRepository groupFsnRepository, GroupRepository groupRepository) {
        this.groupFsnRepository = groupFsnRepository;
        this.groupRepository = groupRepository;
        this.isGroupSegmentedToday = isGroupSegmentedToday;
    }


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Running Group segmentation alert job");
        List<IPGroup> groups = groupRepository.getGroupsForSegmentation();
        List<IPGroup> failedGroups = groups.stream().filter(group -> !isGroupSegmentedToday.test(group)).collect(Collectors.toList());
        String body = "";
        if (failedGroups.size() > 0) {
            log.info("Sending mail for failed Groups");
            failedGroups.forEach((failedGroup) -> body.concat(failedGroup.getName() + "\n"));
        }
        try {
            MailSender.sendMail("nidhigupta.m@flipkart.com", "nidhigupta.m@flipkart.com", "nidhigupta.m@flipkart.com", "localhost", "Failed Segmentation Groups",body,null );
        } catch (Exception e) {
            log.info("Mail for segmentation alert failed" + e);
        }

    }

}
