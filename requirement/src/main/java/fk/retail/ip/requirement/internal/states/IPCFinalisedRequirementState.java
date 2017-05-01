package fk.retail.ip.requirement.internal.states;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.command.download.DownloadIPCFinalisedCommand;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.UploadOverrideFailureLineItem;
import fk.retail.ip.ssl.model.SupplierSelectionResponse;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.util.List;
import java.util.Map;
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
    public List<UploadOverrideFailureLineItem> upload(List<Requirement> requirements,
                                                      List<RequirementDownloadLineItem> parsedJson,
                                                      String userId,
                                                      Map<String, String> fsnToVerticalMap,
                                                      MultiKeyMap<String,SupplierSelectionResponse> fsnWhSupplierMap) {
        throw new UnsupportedOperationException("Invalid Operation");
    }

    @Override
    public Map<String, String> createFsnVerticalMap(List<Requirement> requirements) {
        return null;
    }

    @Override
    public MultiKeyMap<String, SupplierSelectionResponse> createFsnWhSupplierMap(List<RequirementDownloadLineItem> requirementDownloadLineItems, List<Requirement> requirements) {
        return null;
    }
}
