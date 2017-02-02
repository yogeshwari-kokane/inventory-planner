package fk.retail.ip.requirement.internal.states;

import com.google.inject.Inject;

import java.util.List;

import javax.ws.rs.core.StreamingOutput;

import fk.retail.ip.requirement.internal.command.DownloadProposedCommand;
import fk.retail.ip.requirement.internal.entities.Requirement;

/**
 * Created by nidhigupta.m on 30/01/17.
 */
public class ProposedRequirementState implements RequirementState {

    private final DownloadProposedCommand downloadProposedCommand;

    @Inject
    public ProposedRequirementState(DownloadProposedCommand downloadProposedCommand) {
        this.downloadProposedCommand = downloadProposedCommand;
    }

    @Override
    public StreamingOutput download(List<Requirement> requirementList,
                                    boolean isLastAppSupplierRequired,
                                    String requirementState) {
        StreamingOutput output = downloadProposedCommand.withRequirements(requirementList)
                .withLastAppSupplierRequired(isLastAppSupplierRequired)
                .withRequirementState(requirementState)
        .execute();

        return output;
    }

    @Override
    public void upload() {

    }

    @Override
    public void calculate() {

    }
}
