package fk.retail.ip.requirement.internal.enums;

import com.google.inject.Guice;
import com.google.inject.Injector;
import fk.retail.ip.requirement.config.RequirementModule;
import fk.retail.ip.requirement.internal.command.*;
import fk.retail.ip.requirement.internal.command.upload.*;

/**
 * Created by nidhigupta.m on 30/01/17.
 */
public enum RequirementState {

    PROPOSED(DownloadProposedCommand.class, UploadProposedCommand.class),
    CDO_REVIEW(DownloadCDOReviewCommand.class, UploadCDOReviewCommand.class),
    BIZFIN_REVIEW(DownloadBizFinReviewCommand.class, UploadBizFinReviewCommand.class),
    IPC_REVIEW(DownloadIPCReviewCommand.class, UploadIPCReviewCommand.class),
    IPC_FINALIZED(DownloadIPCFinalisedCommand.class, UploadIPCFinalisedCommand.class);

    private DownloadCommand downloadCommand;
    private UploadCommand uploadCommand;

    RequirementState(Class<? extends DownloadCommand> downloadType, Class<? extends UploadCommand> uploadType) {
        Injector INJECTOR = Guice.createInjector(new RequirementModule());
        downloadCommand = INJECTOR.getInstance(downloadType);
        uploadCommand = INJECTOR.getInstance(uploadType);
    }

    public DownloadCommand getDownloadCommand() {
        return this.downloadCommand;
    }
    public UploadCommand getUploadCommand() { return this.uploadCommand; }

}
