package fk.retail.ip.requirement.internal.states;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.command.download.DownloadIPCFinalisedCommand;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.RequirementUploadLineItem;
import fk.retail.ip.requirement.model.UploadOverrideFailureLineItem;
import fk.retail.ip.requirement.model.UploadOverrideResult;
import java.util.List;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 21/02/17.
 */
public class IPCFinalisedRequirementState implements RequirementState {
    private final Provider<DownloadIPCFinalisedCommand> downloadIPCFinalisedCommandProvider;

    @Inject
    public IPCFinalisedRequirementState(Provider<DownloadIPCFinalisedCommand> downloadIPCFinalisedCommandProvider) {
        this.downloadIPCFinalisedCommandProvider = downloadIPCFinalisedCommandProvider;
    }

    @Override
    public StreamingOutput download(List<Requirement> requirements, boolean isLastAppSupplierRequired) {
        return downloadIPCFinalisedCommandProvider.get().execute(requirements, isLastAppSupplierRequired);
    }

    @Override
    public UploadOverrideResult upload(List<Requirement> requirements,
                                       List<RequirementUploadLineItem> parsedJson,
                                       String userId, String state) {
        throw new UnsupportedOperationException("Invalid Operation");
    }

}
