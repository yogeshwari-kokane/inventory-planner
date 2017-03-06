package fk.retail.ip.requirement.internal.command.upload;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.enums.OverrideKeys;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by agarwal.vaibhav on 06/02/17.
 */

@Slf4j
public class UploadCDOReviewCommand extends UploadCommand {

    @Inject
    public UploadCDOReviewCommand(RequirementRepository requirementRepository) {
        super(requirementRepository);
    }

    @Override
    Map<String, Object> validateAndSetStateSpecific(RequirementDownloadLineItem requirementDownloadLineItem) {
        String supplierOverrideReason = requirementDownloadLineItem.getCdoSupplierOverrideReason();
        Integer bdProposedQuantity = requirementDownloadLineItem.getCdoQuantityOverride();
        Integer bdProposedSla = requirementDownloadLineItem.getNewSla();
        Integer bdProposedApp = requirementDownloadLineItem.getCdoPriceOverride();
        String bdProposedSupplier = requirementDownloadLineItem.getCdoSupplierOverride();
        String currentSupplier = requirementDownloadLineItem.getSupplier();
        Integer currentQuantity =  requirementDownloadLineItem.getQuantity();
        Integer currentApp = requirementDownloadLineItem.getApp();
        Integer currentSla = requirementDownloadLineItem.getSla();
        String quantityOverrideComment = requirementDownloadLineItem.getCdoQuantityOverrideReason();
        String appOverrideComment = requirementDownloadLineItem.getCdoPriceOverrideReason();
        Map<String, Object> overriddenValues = new HashMap<>();

        String validationComment = isQuantityOverrideValid(currentQuantity, bdProposedQuantity, quantityOverrideComment);

        validationComment += isSlaOverrideValid(bdProposedSla);

        validationComment += isAppOverrideValid(bdProposedApp, currentApp, appOverrideComment);

        validationComment += isSupplierOverrideValid(bdProposedSupplier, currentSupplier, supplierOverrideReason);

        if (!validationComment.isEmpty()) {
            overriddenValues.put(OverrideKeys.STATUS.toString(), OverrideKeys.FAILURE.toString());
            overriddenValues.put(OverrideKeys.OVERRIDE_COMMENT.toString(), validationComment);
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
                supplierOverrideReason
        );

        return overriddenValues;
    }

//    private Map<String, Object> isValidOverrideQuantity(Object bdProposedQuantity, Object currentQuantity, String quantityOverrideComment) {
//        Map<String, Object> validOverride = new HashMap<>();
//
//        if (bdProposedQuantity == null) {
//            return validOverride;
//        }
//        if ((bdProposedQuantity instanceof Integer) && (Integer) bdProposedQuantity > 0) {
//            if (quantityOverrideComment == null && currentQuantity != bdProposedQuantity) {
//                log.debug("cdo quantity override comment is missing");
//                validOverride.put("failure", "quantity override comment is missing");
//            }
//        } else {
//            log.debug("cdo quantity overridden is less than or equal to zero or not integer");
//            validOverride.put("failure", "quantity is less than zero or not integer");
//        }
//        return validOverride;
//    }

    private String isAppOverrideValid(Integer bdProposedApp, Integer currentApp, String appOverrideComment) {
        String validationComment = new String();

        if (bdProposedApp == null) {
            return validationComment;
        }
        if (bdProposedApp > 0) {
            if (currentApp != bdProposedApp && isEmptyString(appOverrideComment)) {
                log.debug("cdo app override comment is missing");
                validationComment = Constants.APP_OVERRIDE_COMMENT_IS_MISSING;
                //validOverride.put("failure", "app override comment is missing");
            }
        } else {
            if (isEmptyString(appOverrideComment)) {
                validationComment = Constants.APP_OVERRIDE_IS_NOT_GREATER_THAN_ZERO_AND_COMMENT_IS_MISSING.toString();
            } else {
                validationComment = Constants.APP_QUANTITY_IS_NOT_GREATER_THAN_ZERO;
            }
            log.debug(validationComment);
            //validOverride.put("failure", "quantity is less zero or not integer");

        }
        return validationComment;
    }

    private String isSupplierOverrideValid(String bdProposedSupplier, String currentSupplier, String supplierOverrideReason) {
        String validationComment = new String();

        if (bdProposedSupplier == null) {
            return validationComment;
        }
        if (bdProposedSupplier != currentSupplier && isEmptyString(supplierOverrideReason)) {
            if (currentSupplier == null) {
                //validOverride.put("failure", "override comment is missing and supplier overridden from blank");
                validationComment = Constants.SUPPLIER_OVERRIDE_COMMENT_IS_MISSING_WHEN_UPDATED_FROM_BLANK;
            } else {
                //validOverride.put("failure", "override comment is missing");
                validationComment = Constants.SUPPLIER_OVERRIDE_COMMENT_IS_MISSING;
            }
            log.debug("cdo supplier override comment is missing");
        }
        return validationComment;
    }

    private String isSlaOverrideValid(Integer bdProposedSla) {
        String validationComment = new String();
        if (bdProposedSla == null) {
            return validationComment;
        }

        if (bdProposedSla <= 0){
            log.debug("sla is less than or equal to zero");
            //validOverride.put("failure", "sla is less than zero or not Integer");
            validationComment = Constants.SLA_QUANTITY_IS_NOT_GREATER_THAN_ZERO;
        }
        return validationComment;
    }

    private Map<String, Object> getOverriddenFields(
            Object currentQuantity,
            Object currentSupplier,
            Object currentApp,
            Object currentSla,
            Object bdProposedQuantity,
            Object bdProposedApp,
            Object bdProposedSla,
            Object bdProposedSupplier,
            String quantityOverrideComment,
            String appOverrideComment,
            String supplierOverrideComment
    ) {

        Map<String, Object> overriddenValues = new HashMap<>();
        JSONObject overrideCommentJson = new JSONObject();
        overriddenValues.put(OverrideKeys.STATUS.toString(), OverrideKeys.SUCCESS.toString());


        if (bdProposedQuantity != null && bdProposedQuantity != currentQuantity) {
            Integer quantityToUse = (Integer) bdProposedQuantity;
            overriddenValues.put(OverrideKeys.QUANTITY.toString(), quantityToUse);
            overrideCommentJson.put(Constants.QUANTITY_OVERRIDE_COMMENT.toString(), quantityOverrideComment);
            overriddenValues.put(OverrideKeys.STATUS.toString(), OverrideKeys.UPDATE.toString());
        }

        if (bdProposedSupplier != null && bdProposedSupplier != currentSupplier) {
            String supplierToUse = bdProposedSupplier.toString();
            overriddenValues.put(OverrideKeys.SUPPLIER.toString(),supplierToUse);
            overrideCommentJson.put(Constants.SUPPLIER_OVERRIDE_COMMENT.toString(), supplierOverrideComment);
            overriddenValues.put(OverrideKeys.STATUS.toString(), OverrideKeys.UPDATE.toString());
        }

        if (bdProposedApp != null && bdProposedApp != currentApp) {
            Integer appToUse = (Integer) bdProposedApp;
            overriddenValues.put(OverrideKeys.APP.toString(), appToUse);
            overrideCommentJson.put(Constants.APP_OVERRIDE_COMMENT.toString(), appOverrideComment);
            overriddenValues.put(OverrideKeys.STATUS.toString(), OverrideKeys.UPDATE.toString());
        }

        if (bdProposedSla != null && bdProposedSla != currentSla) {
            Integer slaToUse = (Integer) bdProposedSla;
            overriddenValues.put(OverrideKeys.SLA.toString(), slaToUse);
            overriddenValues.put(OverrideKeys.STATUS.toString(), OverrideKeys.UPDATE.toString());
        }

        overriddenValues.put(OverrideKeys.OVERRIDE_COMMENT.toString(), overrideCommentJson);

        return overriddenValues;
    }


}
