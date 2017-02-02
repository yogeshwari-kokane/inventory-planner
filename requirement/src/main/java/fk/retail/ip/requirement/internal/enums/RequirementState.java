package fk.retail.ip.requirement.internal.enums;

import com.google.inject.Guice;
import com.google.inject.Injector;
import fk.retail.ip.requirement.config.RequirementModule;
import fk.retail.ip.requirement.internal.command.DownloadBizFinReviewCommand;
import fk.retail.ip.requirement.internal.command.DownloadCDOReviewCommand;
import fk.retail.ip.requirement.internal.command.DownloadCommand;
import fk.retail.ip.requirement.internal.command.DownloadIPCFinalisedCommand;
import fk.retail.ip.requirement.internal.command.DownloadIPCReviewCommand;
import fk.retail.ip.requirement.internal.command.DownloadProposedCommand;

/**
 * Created by nidhigupta.m on 30/01/17.
 */
public enum RequirementState {

    proposed,
    CDOReview,
    BizFinReview,
    IPCReview,
    IPCFinalised;

    private static final Injector INJECTOR;
    private static final DownloadCommand downloadProposedCommand;
    private static final DownloadCommand downloadCDOReviewCommand;
    private static final DownloadCommand downloadBizFinReviewCommand;
    private static final DownloadCommand downloadIPCReviewCommand;
    private static final DownloadCommand downloadIPCFinalisedCommand;
    private DownloadCommand downloadCommand;

    static {
        INJECTOR = Guice.createInjector(new RequirementModule());
        downloadProposedCommand = INJECTOR.getInstance(DownloadProposedCommand.class);
        downloadCDOReviewCommand = INJECTOR.getInstance(DownloadCDOReviewCommand.class);
        downloadBizFinReviewCommand = INJECTOR.getInstance(DownloadBizFinReviewCommand.class);
        downloadIPCReviewCommand = INJECTOR.getInstance(DownloadIPCReviewCommand.class);
        downloadIPCFinalisedCommand = INJECTOR.getInstance(DownloadIPCFinalisedCommand.class);
    }
    static {
        proposed.downloadCommand = downloadProposedCommand;
        CDOReview.downloadCommand = downloadCDOReviewCommand;
        BizFinReview.downloadCommand = downloadBizFinReviewCommand;
        IPCReview.downloadCommand = downloadIPCReviewCommand;
        IPCFinalised.downloadCommand = downloadIPCFinalisedCommand;
    }

    public DownloadCommand getDownloadCommand() {
        return this.downloadCommand;
    }

    public String getDownloadTemplate() {
        return this.name() + ".xlsx";
    }


}
