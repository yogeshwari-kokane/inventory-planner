package fk.retail.ip.projection.internal.command;

import fk.retail.ip.projection.internal.repository.OverrideEventRepository;

/**
 * Created by nidhigupta.m on 08/01/17.
 */
public class OverrideBDApprovedProjectionCommand extends OverrideProjectionCommand {
    public OverrideBDApprovedProjectionCommand(OverrideEventRepository overrideEventRepository) {
        super(overrideEventRepository);
    }

    @Override
    public void execute() {

    }
}
