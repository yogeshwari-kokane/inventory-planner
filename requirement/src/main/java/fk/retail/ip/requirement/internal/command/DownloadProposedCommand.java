package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class DownloadProposedCommand extends DownloadCommand {


    @Inject
    public DownloadProposedCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository) {
        super(fsnBandRepository, weeklySaleRepository);
    }

    @Override
    protected String getTemplateName() {
        return "proposed.xlsx";
    }

    @Override
    void fetchRequirementStateData() {

    }
}
