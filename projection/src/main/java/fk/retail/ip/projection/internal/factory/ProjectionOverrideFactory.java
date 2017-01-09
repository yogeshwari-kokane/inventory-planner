package fk.retail.ip.projection.internal.factory;

import com.google.inject.Provider;

import fk.retail.ip.projection.internal.command.OverrideApprovedProjectionCommand;
import fk.retail.ip.projection.internal.command.OverrideBDApprovedProjectionCommand;
import fk.retail.ip.projection.internal.command.OverrideProjectionCommand;
import fk.retail.ip.projection.internal.command.OverrideVerifiedProjectionCommand;
import fk.retail.ip.projection.internal.enums.ProjectionApprovalState;
import fk.retail.ip.projection.internal.exception.ProjectionOverrideException;

/**
 * Created by nidhigupta.m on 08/01/17.
 */
public class ProjectionOverrideFactory {

    private final Provider<OverrideApprovedProjectionCommand> overrideApprovedProjectionCommandProvider;
    private final Provider<OverrideBDApprovedProjectionCommand> overrideBDApprovedProjectionCommandProvider;
    private final Provider<OverrideVerifiedProjectionCommand> overrideVerifiedProjectionCommandProvider;

    public ProjectionOverrideFactory(Provider<OverrideApprovedProjectionCommand> overrideApprovedProjectionCommandProvider,
                                     Provider<OverrideVerifiedProjectionCommand> overrideVerifiedProjectionCommandProvider,
                                     Provider<OverrideBDApprovedProjectionCommand> overrideBDApprovedProjectionCommandProvider){
        this.overrideApprovedProjectionCommandProvider = overrideApprovedProjectionCommandProvider;
        this.overrideBDApprovedProjectionCommandProvider = overrideBDApprovedProjectionCommandProvider;
        this.overrideVerifiedProjectionCommandProvider = overrideVerifiedProjectionCommandProvider;
    }


    public OverrideProjectionCommand getStateOverrideProjectionCommand(String currentState) throws ProjectionOverrideException {
        if (!ProjectionApprovalState.isValidOverrideState(currentState)) {
            throw new ProjectionOverrideException("Cannot upload projection override in state " + currentState);
        }

        if (currentState.equals(ProjectionApprovalState.VERIFIED)) {
            return overrideVerifiedProjectionCommandProvider.get();
        } else if (currentState.equals(ProjectionApprovalState.APPROVED)) {
            return overrideApprovedProjectionCommandProvider.get();
        } else if (currentState.equals(ProjectionApprovalState.BD_APPROVED)) {
            return overrideBDApprovedProjectionCommandProvider.get();
        } return null;
    }

}
