package fk.retail.ip.requirement.internal.states;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.command.download.DownloadBizFinReviewCommand;
import fk.retail.ip.requirement.internal.command.upload.BizFinReviewUploadCommand;
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
public class BizFinRequirementState implements RequirementState {
    private final Provider<DownloadBizFinReviewCommand> downloadBizFinReviewCommandProvider;
    private final Provider<BizFinReviewUploadCommand> uploadBizFinReviewCommandProvider;

    @Inject
    public BizFinRequirementState(Provider<DownloadBizFinReviewCommand> downloadBizFinReviewCommandProvider, Provider<BizFinReviewUploadCommand> uploadBizFinReviewCommandProvider) {
        this.downloadBizFinReviewCommandProvider = downloadBizFinReviewCommandProvider;
        this.uploadBizFinReviewCommandProvider = uploadBizFinReviewCommandProvider;
    }

    @Override
    public UploadOverrideResult upload(List<Requirement> requirements,
                                       List<RequirementUploadLineItem> parsedJson,
                                       String userId, String state) {
        return uploadBizFinReviewCommandProvider.get().execute(parsedJson, requirements, userId, state);
    }

    @Override
    public StreamingOutput download(List<Requirement> requirements, boolean isLastAppSupplierRequired) {
        return downloadBizFinReviewCommandProvider.get().execute(requirements, isLastAppSupplierRequired);
    }
}
