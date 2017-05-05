package fk.retail.ip.requirement.internal.command.upload;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.command.FdpRequirementIngestorImpl;
import fk.retail.ip.requirement.internal.entities.RequirementEventLog;
import fk.retail.ip.requirement.internal.enums.OverrideKey;
import fk.retail.ip.requirement.internal.enums.OverrideStatus;
import fk.retail.ip.requirement.internal.repository.RequirementEventLogRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.RequirementUploadLineItem;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by agarwal.vaibhav on 06/02/17.
 */

@Slf4j
public class CDOReviewUploadCommand extends UploadCommand {

    @Inject
    public CDOReviewUploadCommand(
            RequirementRepository requirementRepository,
            FdpRequirementIngestorImpl fdpRequirementIngestor,
            RequirementEventLogRepository requirementEventLogRepository
    ) {
        super(requirementRepository, fdpRequirementIngestor, requirementEventLogRepository);
    }

    @Override
    Map<String, Object> validateAndSetStateSpecificFields(RequirementUploadLineItem requirementUploadLineItem) {
        String supplierOverrideComment = requirementUploadLineItem.getCdoSupplierOverrideReason();
        Object bdProposedQuantity = requirementUploadLineItem.getCdoQuantityOverride();
        Object bdProposedSla = requirementUploadLineItem.getNewSla();
        Object bdProposedApp = requirementUploadLineItem.getCdoPriceOverride();
        String bdProposedSupplier = requirementUploadLineItem.getCdoSupplierOverride();
        String currentSupplier = requirementUploadLineItem.getSupplier();
        Integer currentQuantity =  requirementUploadLineItem.getQuantity();
        Double currentApp = requirementUploadLineItem.getApp();
        Integer currentSla = requirementUploadLineItem.getSla();
        String quantityOverrideComment = requirementUploadLineItem.getCdoQuantityOverrideReason();
        String appOverrideComment = requirementUploadLineItem.getCdoPriceOverrideReason();
        Map<String, Object> overriddenValues = new HashMap<>();

        String validationComment = "";

        Optional<String> validationResponse = validateQuantityOverride(
                currentQuantity,
                bdProposedQuantity,
                quantityOverrideComment
        );

        if (validationResponse.isPresent()) {
            validationComment = convertToLineSeparatedComment(validationComment, validationResponse.get());
        }

        validationResponse = validateSlaOverride(bdProposedSla);
        if (validationResponse.isPresent()) {
            validationComment = convertToLineSeparatedComment(validationComment, validationResponse.get());
        }

        validationResponse = validateAppOverride(bdProposedApp, currentApp, appOverrideComment);
        if (validationResponse.isPresent()) {
            validationComment = convertToLineSeparatedComment(validationComment, validationResponse.get());
        }

        validationResponse = validateSupplierOverride(bdProposedSupplier, currentSupplier, supplierOverrideComment);
        if (validationResponse.isPresent()) {
            validationComment = convertToLineSeparatedComment(validationComment, validationResponse.get());
        }

        if (!validationComment.isEmpty()) {
            overriddenValues.put(Constants.STATUS, OverrideStatus.FAILURE.toString());
            overriddenValues.put(OverrideKey.OVERRIDE_COMMENT.toString(), validationComment);
            return overriddenValues;
        }

        overriddenValues = getOverriddenFields(
                currentQuantity,
                currentSupplier,
                currentApp,
                currentSla,
                bdProposedQuantity,
                bdProposedApp,
                bdProposedSla,
                bdProposedSupplier,
                quantityOverrideComment,
                appOverrideComment,
                supplierOverrideComment
        );

        return overriddenValues;
    }

    private Optional<String> validateAppOverride(Object bdProposedApp, Double currentApp, String appOverrideComment) {
        String validationComment;
        if (bdProposedApp == null) {
            return Optional.empty();
        }

        if (bdProposedApp instanceof Double) {
            if ((double)bdProposedApp <= 0) {
                validationComment = isEmptyString(appOverrideComment) ?
                        Constants.INVALID_APP_WITHOUT_COMMENT :
                        Constants.APP_QUANTITY_IS_NOT_GREATER_THAN_ZERO;
                return Optional.of(validationComment);
            } else if(bdProposedApp != currentApp && isEmptyString(appOverrideComment)) {
                validationComment = Constants.APP_OVERRIDE_COMMENT_IS_MISSING;
                return Optional.of(validationComment);
            } else {
                return Optional.empty();
            }
        } else {
            validationComment = isEmptyString(appOverrideComment) ? Constants.INVALID_APP_WITHOUT_COMMENT :
                    Constants.APP_IS_NOT_VALID;
            return Optional.of(validationComment);
        }

    }

    private Optional<String> validateSupplierOverride(
            String bdProposedSupplier,
            String currentSupplier,
            String supplierOverrideComment
    ) {
        String validationComment;

        if (isEmptyString(bdProposedSupplier)) {
            return Optional.empty();
        }
        if (bdProposedSupplier != currentSupplier && isEmptyString(supplierOverrideComment)) {
            validationComment = Constants.SUPPLIER_OVERRIDE_COMMENT_IS_MISSING;
            return Optional.of(validationComment);
        }
        return Optional.empty();
    }

    private Optional<String> validateSlaOverride(Object bdProposedSla) {
        String validationComment;
        if (bdProposedSla == null) {
            return Optional.empty();
        }

        if (bdProposedSla instanceof Integer) {
            if ((int)bdProposedSla <= 0){
                validationComment = Constants.SLA_QUANTITY_IS_NOT_GREATER_THAN_ZERO;
                return Optional.of(validationComment);
            } else {
                return Optional.empty();
            }
        } else {
            validationComment = Constants.SLA_IS_NOT_INTEGER;
            return Optional.of(validationComment);
        }
    }

    private Map<String, Object> getOverriddenFields(
            Integer currentQuantity,
            String currentSupplier,
            Double currentApp,
            Integer currentSla,
            Object bdProposedQuantity,
            Object bdProposedApp,
            Object bdProposedSla,
            String bdProposedSupplier,
            String quantityOverrideComment,
            String appOverrideComment,
            String supplierOverrideComment
    ) {

        Map<String, Object> overriddenValues = new HashMap<>();
        JSONObject overrideComment = new JSONObject();
        overriddenValues.put(Constants.STATUS, OverrideStatus.SUCCESS.toString());


        if (bdProposedQuantity != null && bdProposedQuantity != currentQuantity) {
            Integer quantityToUse = (Integer) bdProposedQuantity;
            overriddenValues.put(OverrideKey.QUANTITY.toString(), quantityToUse);
            overrideComment.put(Constants.QUANTITY_OVERRIDE_COMMENT, quantityOverrideComment);
            overriddenValues.put(Constants.STATUS, OverrideStatus.UPDATE.toString());
        }

        if (!isEmptyString(bdProposedSupplier) && bdProposedSupplier != currentSupplier) {
            String supplierToUse = bdProposedSupplier.toString();
            overriddenValues.put(OverrideKey.SUPPLIER.toString(),supplierToUse);
            overrideComment.put(Constants.SUPPLIER_OVERRIDE_COMMENT, supplierOverrideComment);
            overriddenValues.put(Constants.STATUS, OverrideStatus.UPDATE.toString());
        }

        if (bdProposedApp != null && bdProposedApp != currentApp) {
            Double appToUse = (Double) bdProposedApp;
            overriddenValues.put(OverrideKey.APP.toString(), appToUse);
            overrideComment.put(Constants.APP_OVERRIDE_COMMENT, appOverrideComment);
            overriddenValues.put(Constants.STATUS, OverrideStatus.UPDATE.toString());
        }

        if (bdProposedSla != null && bdProposedSla != currentSla) {
            Integer slaToUse = (Integer) bdProposedSla;
            overriddenValues.put(OverrideKey.SLA.toString(), slaToUse);
            overriddenValues.put(Constants.STATUS, OverrideStatus.UPDATE.toString());
        }

        overriddenValues.put(OverrideKey.OVERRIDE_COMMENT.toString(), overrideComment);

        return overriddenValues;
    }

    private String convertToLineSeparatedComment(String firstString, String secondString) {
        return firstString.isEmpty() ? secondString : firstString + System.lineSeparator() + secondString;
    }

}
