package fk.retail.ip.requirement.internal.command.upload;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by agarwal.vaibhav on 06/02/17.
 */

public class UploadCDOReviewCommand extends UploadCommand {

    @Override
    Map<String, Object> validateAndSetStateSpecific(Map<String, Object> row) {
        String supplierOverrideReason = row.get("bd_supplier_override_reason").toString();
        Object bdProposedQuantity = row.get("bd_quantity");
        Object bdProposedSla = row.get("new_sla");
        Object bdProposedApp = row.get("bd_app");
        Object bdProposedSupplier = row.get("bd_supplier");
        Object currentSupplier = row.get("supplier");
        Object currentQuantity =  row.get("quantity");
        Object currentApp = row.get("app");
        Object currentSla = row.get("sla");
        String quantityOverrideComment = row.get("bd_quantity_override_reason").toString();
        String appOverrideComment = row.get("bd_app_override_reason").toString();
        String slaOverrideComment = row.get("sla_override_comment").toString();
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
                supplierOverrideReason,
                slaOverrideComment
        );

        return overriddenValues;
    }

    private Map<String, Object> isValidOverrideQuantity(Object bdProposedQuantity, Object currentQuantity, String quantityOverrideComment) {
        Map<String, Object> validOverride = new HashMap<>();

        if (bdProposedQuantity == null) {
            return validOverride;
        }
        if ((bdProposedQuantity instanceof Integer) && (Integer) bdProposedQuantity > 0) {
            if (quantityOverrideComment.isEmpty() && currentQuantity != bdProposedQuantity) {
                //log => override comment is missing
                validOverride.put("failure", "quantity override comment is missing");
            }
        } else {
            validOverride.put("failure", "quantity is less than zero or not integer");
            //log => quantity is less than zero or not integer
        }
        return validOverride;
    }

    private Map<String, Object> isValidOverrideApp(Object bdProposedApp, Object currentApp, String appOverrideComment) {
        Map<String, Object> validOverride = new HashMap<>();
        if (bdProposedApp == null) {
            return validOverride;
        }
        if ((bdProposedApp instanceof Integer) && (Integer) bdProposedApp > 0) {
            if (appOverrideComment.isEmpty() && currentApp != bdProposedApp) {
                //log => comment is not present
                validOverride.put("failure", "app override comment is missing");
            }
            //log => app is not a positive number or not integer
        } else {
            //log => quantity is less than zero or not integer
            validOverride.put("failure", "quantity is less zero or not integer");
        }
        return validOverride;
    }

    private Map<String, Object> isValidOverrideSupplier(Object bdProposedSupplier, Object currentSupplier, String supplierOverrideReason) {
        Map<String, Object> validOverride = new HashMap<>();
        if (bdProposedSupplier == null) {
            return validOverride;
        }
        if (supplierOverrideReason.isEmpty() && bdProposedSupplier != currentSupplier) {
            if (currentSupplier == null) {
                validOverride.put("failure", "override comment is missing and supplier overridden from blank");
                //log => override comment is missing and supplier overridden from blank
            } else {
                validOverride.put("failure", "override comment is missing");
                //log => override comment is missing
            }
        }
        return validOverride;
    }

    private Map<String, Object> isValidOverrideSla(Object bdProposedSla) {
        Map<String, Object> validOverride = new HashMap<>();
        if (bdProposedSla == null) {
            return validOverride;
        }
        if ((bdProposedSla instanceof Integer) && (Integer) bdProposedSla > 0) {
            return validOverride;
        } else {
            //log => sla is less than zero or not integer
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
            String supplierOverrideComment,
            String slaOverrideComment
    ) {

        Map<String, Object> overriddenValues = new HashMap<>();

        JSONArray commentsArray = new JSONArray();


        if (bdProposedQuantity != null && bdProposedQuantity != currentQuantity) {
            Integer quantityToUse = (Integer) bdProposedQuantity;
            overriddenValues.put("quantity", quantityToUse);
            JSONObject quantityOverrideJson = new JSONObject();
            quantityOverrideJson.put("quantityOverrideComment", quantityOverrideComment);
            commentsArray.put(quantityOverrideJson);
        }

        if (bdProposedSupplier != null && bdProposedSupplier != currentSupplier) {
            String supplierToUse = bdProposedSupplier.toString();
            overriddenValues.put("supplier",supplierToUse);
            JSONObject supplierOverrideJson = new JSONObject();
            supplierOverrideJson.put("supplierOverrideComment", supplierOverrideComment);
            commentsArray.put(supplierOverrideJson);
        }

        if (bdProposedApp != null && bdProposedApp != currentApp) {
            Integer appToUse = (Integer) bdProposedApp;
            overriddenValues.put("app", appToUse);
            JSONObject appOverrideJson = new JSONObject();
            appOverrideJson.put("appOverrideComment", appOverrideComment);
            commentsArray.put(appOverrideJson);
        }

        if (bdProposedSla != null && bdProposedSla != currentSla) {
            Integer slaToUse = (Integer) bdProposedSla;
            overriddenValues.put("sla", slaToUse);
            JSONObject slaOverrideJson = new JSONObject();
            slaOverrideJson.put("slaOverrideComment", slaOverrideComment);
            commentsArray.put(slaOverrideJson);
        }
        overriddenValues.put("overrideComment", commentsArray);
        return overriddenValues;
    }


}
