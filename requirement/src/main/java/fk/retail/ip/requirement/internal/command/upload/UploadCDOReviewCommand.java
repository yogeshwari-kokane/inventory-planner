package fk.retail.ip.requirement.internal.command.upload;

import com.google.inject.Inject;
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
        Object bdProposedQuantity = requirementDownloadLineItem.getCdoQuantityOverride();
        Object bdProposedSla = requirementDownloadLineItem.getNewSla();
        Object bdProposedApp = requirementDownloadLineItem.getCdoPriceOverride();
        Object bdProposedSupplier = requirementDownloadLineItem.getCdoSupplierOverride();
        Object currentSupplier = requirementDownloadLineItem.getSupplier();
        Object currentQuantity =  requirementDownloadLineItem.getQuantity();
        Object currentApp = requirementDownloadLineItem.getApp();
        Object currentSla = requirementDownloadLineItem.getSla();
        String quantityOverrideComment = requirementDownloadLineItem.getCdoQuantityOverrideReason();
        String appOverrideComment = requirementDownloadLineItem.getCdoPriceOverrideReason();
        Map<String, Object> overriddenValues;

        overriddenValues = isValidOverrideQuantity(bdProposedQuantity, currentQuantity, quantityOverrideComment);
        if (!overriddenValues.isEmpty()) {
            return overriddenValues;
        }

        overriddenValues = isValidOverrideSla(bdProposedSla);
        if (!overriddenValues.isEmpty()) {
            return overriddenValues;
        }

        overriddenValues = isValidOverrideApp(bdProposedApp, currentApp, appOverrideComment);
        if (!overriddenValues.isEmpty()) {
            return overriddenValues;
        }

        overriddenValues = isValidOverrideSupplier(bdProposedSupplier, currentSupplier, supplierOverrideReason);
        if (!overriddenValues.isEmpty()) {
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

    private Map<String, Object> isValidOverrideQuantity(Object bdProposedQuantity, Object currentQuantity, String quantityOverrideComment) {
        Map<String, Object> validOverride = new HashMap<>();

        if (bdProposedQuantity == null) {
            return validOverride;
        }
        if ((bdProposedQuantity instanceof Integer) && (Integer) bdProposedQuantity > 0) {
            if (quantityOverrideComment == null && currentQuantity != bdProposedQuantity) {
                log.debug("cdo quantity override comment is missing");
                validOverride.put("failure", "quantity override comment is missing");
            }
        } else {
            log.debug("cdo quantity overridden is less than or equal to zero or not integer");
            validOverride.put("failure", "quantity is less than zero or not integer");
        }
        return validOverride;
    }

    private Map<String, Object> isValidOverrideApp(Object bdProposedApp, Object currentApp, String appOverrideComment) {
        Map<String, Object> validOverride = new HashMap<>();
        if (bdProposedApp == null) {
            return validOverride;
        }
        if ((bdProposedApp instanceof Integer) && (Integer) bdProposedApp > 0) {
            if (appOverrideComment == null && currentApp != bdProposedApp) {
                log.debug("cdo app override comment is missing");
                validOverride.put("failure", "app override comment is missing");
            }
        } else {
            log.debug("app is less than or equal to zero or not integer");
            validOverride.put("failure", "quantity is less zero or not integer");
        }
        return validOverride;
    }

    private Map<String, Object> isValidOverrideSupplier(Object bdProposedSupplier, Object currentSupplier, String supplierOverrideReason) {
        Map<String, Object> validOverride = new HashMap<>();
        if (bdProposedSupplier == null) {
            return validOverride;
        }
        if (supplierOverrideReason == null && bdProposedSupplier != currentSupplier) {
            if (currentSupplier == null) {
                validOverride.put("failure", "override comment is missing and supplier overridden from blank");
            } else {
                validOverride.put("failure", "override comment is missing");
            }
            log.debug("cdo supplier override comment is missing");
        }
        return validOverride;
    }

    private Map<String, Object> isValidOverrideSla(Object bdProposedSla) {
        Map<String, Object> validOverride = new HashMap<>();
        if (bdProposedSla == null) {
            return validOverride;
        }
        if ((bdProposedSla instanceof Integer) && (Integer) bdProposedSla > 0) {

        } else {
            log.debug("sla is less than or equal to zero or not integer");
            validOverride.put("failure", "sla is less than zero or not Integer");
        }
        return validOverride;
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


        if (bdProposedQuantity != null && bdProposedQuantity != currentQuantity) {
            Integer quantityToUse = (Integer) bdProposedQuantity;
            overriddenValues.put("quantity", quantityToUse);
            overrideCommentJson.put("quantityOverrideComment", quantityOverrideComment);
        }

        if (bdProposedSupplier != null && bdProposedSupplier != currentSupplier) {
            String supplierToUse = bdProposedSupplier.toString();
            overriddenValues.put("supplier",supplierToUse);
            overrideCommentJson.put("supplierOverrideComment", supplierOverrideComment);
        }

        if (bdProposedApp != null && bdProposedApp != currentApp) {
            Integer appToUse = (Integer) bdProposedApp;
            overriddenValues.put("app", appToUse);
            overrideCommentJson.put("appOverrideComment", appOverrideComment);
        }

        if (bdProposedSla != null && bdProposedSla != currentSla) {
            Integer slaToUse = (Integer) bdProposedSla;
            overriddenValues.put("sla", slaToUse);
        }
        overriddenValues.put("overrideComment", overrideCommentJson);
        return overriddenValues;
    }


}
