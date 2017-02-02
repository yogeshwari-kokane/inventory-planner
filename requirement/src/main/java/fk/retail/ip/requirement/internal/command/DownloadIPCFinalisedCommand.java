package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class DownloadIPCFinalisedCommand extends DownloadCommand{

    @Inject
    public DownloadIPCFinalisedCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository) {
        super(fsnBandRepository, weeklySaleRepository);
    }

    @Override
    void fetchRequirementStateData() {

    }
}
