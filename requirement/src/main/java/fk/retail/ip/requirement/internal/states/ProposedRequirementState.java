package fk.retail.ip.requirement.internal.states;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.command.download.DownloadProposedCommand;
import fk.retail.ip.requirement.internal.command.upload.ProposedUploadCommand;
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
public class ProposedRequirementState implements RequirementState {

    private final Provider<DownloadProposedCommand> downloadProposedCommandProvider;
    private final Provider<ProposedUploadCommand> uploadProposedCommandProvider;

    @Inject
    public ProposedRequirementState(Provider<DownloadProposedCommand> downloadProposedCommandProvider, Provider<ProposedUploadCommand> uploadProposedCommandProvider) {
        this.downloadProposedCommandProvider = downloadProposedCommandProvider;
        this.uploadProposedCommandProvider = uploadProposedCommandProvider;
    }

    @Override
    public List<UploadOverrideFailureLineItem> upload(List<Requirement> requirements,
                                                      List<RequirementDownloadLineItem> parsedJson,
                                                      String userId,
                                                      Map<String, String> fsnToVerticalMap,
                                                      MultiKeyMap<String,SupplierSelectionResponse> fsnWhSupplierMap) {
        return uploadProposedCommandProvider.get().execute(parsedJson, requirements, userId, fsnToVerticalMap, fsnWhSupplierMap);
    }

    @Override
    public Map<String, String> createFsnVerticalMap(List<Requirement> requirements) {
        return null;
    }

    @Override
    public MultiKeyMap<String, SupplierSelectionResponse> createFsnWhSupplierMap(List<RequirementDownloadLineItem> requirementDownloadLineItems, List<Requirement> requirements) {
        return null;
    }

    @Override
    public StreamingOutput download(List<Requirement> requirements, boolean isLastAppSupplierRequired) {
        return downloadProposedCommandProvider.get().execute(requirements, isLastAppSupplierRequired);
    }
}
