package fk.retail.ip.requirement.internal.command;

import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class DownloadIPCReviewCommand extends DownloadCommand {

    public DownloadIPCReviewCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository) {
        super(fsnBandRepository, weeklySaleRepository);
    }

    @Override
    void fetchRequirementStateData() {

    }
}
