package fk.retail.ip.requirement.internal.states;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.command.download.DownloadCDOReviewCommand;
import fk.retail.ip.requirement.internal.command.upload.CdoReviewUploadCommand;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.RequirementUploadLineItem;

import java.io.IOException;
import java.util.List;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 21/02/17.
 */
public class CDOReviewRequirementState implements RequirementState {
    private final Provider<DownloadCDOReviewCommand> downloadCDOReviewCommandProvider;
    private final Provider<CdoReviewUploadCommand> uploadCDOReviewCommandProvider;

    @Inject
    public CDOReviewRequirementState(Provider<DownloadCDOReviewCommand> downloadCDOReviewCommandProvider, Provider<CdoReviewUploadCommand> uploadCDOReviewCommandProvider) {
        this.downloadCDOReviewCommandProvider = downloadCDOReviewCommandProvider;
        this.uploadCDOReviewCommandProvider = uploadCDOReviewCommandProvider;
    }

//    @Inject
//    public CDOReviewRequirementState(Provider<UploadCDOReviewCommand> uploadCDOReviewCommandProvider) {
//        this.uploadCDOReviewCommandProvider = uploadCDOReviewCommandProvider;
//    }

    @Override
    public List<RequirementUploadLineItem> upload(List<Requirement> requirements, List<RequirementDownloadLineItem> parsedJson) throws IOException {
        return uploadCDOReviewCommandProvider.get().execute(parsedJson, requirements);
    }

    @Override
    public StreamingOutput download(List<Requirement> requirements, boolean isLastAppSupplierRequired) {
        return downloadCDOReviewCommandProvider.get().execute(requirements, isLastAppSupplierRequired);
    }
}
