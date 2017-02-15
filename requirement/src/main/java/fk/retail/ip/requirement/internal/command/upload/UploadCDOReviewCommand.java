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
    String validateStateSpecific(Map<String, Object> row) {
        String supplierOverrideReason = row.get("bd_supplier_override_reason").toString();
        Object bdProposedQuantity = row.get("bd_quantity");
        Object bdProposedSla = row.get("new_sla");
        Object bdProposedApp = row.get("bd_app");
        Object bdProposedSupplier = row.get("bd_supplier");
        Object currentSupplier = row.get("supplier");
        Object currentQuantity =  row.get("quantity");
        Object currentApp = row.get("app");
        String quantityOverrideComment = row.get("bd_quantity_override_reason").toString();
        String appOverrideComment = row.get("bd_app_override_reason").toString();
        String comment;

        comment = isValidOverrideQuantity(bdProposedQuantity, currentQuantity, quantityOverrideComment);
        if (comment != null) {
            return comment;
        }

        comment = isValidOverrideSla(bdProposedSla);
        if (comment != null) {
            return comment;
        }

        comment = isValidOverrideApp(bdProposedApp, currentApp, appOverrideComment);
        if (comment != null) {
            return comment;
        }

        comment = isValidOverrideSupplier(bdProposedSupplier, currentSupplier, supplierOverrideReason);
        if (comment != null) {
            return comment;
        }

        return comment;
    }

    String isValidOverrideQuantity(Object stateQuantity, Object currentQuantity, String quantityOverrideComment) {
        if (stateQuantity == null) {
            return null;
        }
        if ((stateQuantity instanceof Integer) && (Integer) stateQuantity > 0) {
            if (quantityOverrideComment.isEmpty() && stateQuantity != currentQuantity) {
                //log => override comment is missing
                return "quantity override comment is missing";
            }
            return null;
        } else {
            //log => quantity is less than zero or not integer
            return "quantity is less than zero or not integer";
        }
    }

    private String isValidOverrideApp(Object stateApp, Object currentApp, String appOverrideComment) {

        if (stateApp == null) {
            return null;
        }
        if ((stateApp instanceof Integer) && (Integer) stateApp > 0) {
            if (appOverrideComment.isEmpty() && currentApp != stateApp) {
                //log => comment is not present
                return "app override comment is missing";
            }
            //log => app is not a positive number or not integer
            return null;
        } else {
            //log => quantity is less than zero or not integer
            return "quantity is less zero or not integer";
        }
    }

    private String isValidOverrideSupplier(Object stateSupplier, Object currentSupplier, String supplierOverrideReason) {
        if (stateSupplier == null) {
            return null;
        }
        if (supplierOverrideReason.isEmpty() && stateSupplier != currentSupplier) {
            if (currentSupplier == null) {
                return "override comment is missing and supplier overridden from blank";
                //log => override comment is missing and supplier overridden from blank
            } else {
                return "override comment is missing";
                //log => override comment is missing
            }
        }
        return null;
    }

    private String isValidOverrideSla(Object stateSla) {
        if (stateSla == null) {
            return null;
        }
        if ((stateSla instanceof Integer) && (Integer) stateSla > 0) {
            return null;
        } else {
            //log => sla is less than zero or not integer
            return "sla is less than zero or not Integer";
        }
    }

    @Override
    Map<String, Object> getOverriddenFields(Map<String, Object> row) {

        Map<String, Object> overriddenValues = new HashMap<>();
        String fsn = row.get("fsn").toString();
        String sku = row.get("sku").toString();
        String supplier = row.get("supplier").toString();

        Object quantityOverridden = row.get("bd_quantity");
        Object originalQuantity = row.get("quantity");

        JSONArray commentsArray = new JSONArray();


        if (quantityOverridden != null && quantityOverridden != originalQuantity) {
            Integer proposedQuantity = (Integer) quantityOverridden;
            overriddenValues.put("quantity", proposedQuantity);
            JSONObject quantityOverrideComment = new JSONObject();
            quantityOverrideComment.put("quantityOverrideComment", quantityOverrideComment);
        }

        Object supplierOverridden = row.get("bd_supplier");
        Object originalSupplier = row.get("supplier");
        if (supplierOverridden != null && supplierOverridden != originalSupplier) {
            String proposedSupplier = supplierOverridden.toString();
            overriddenValues.put("supplier", proposedSupplier);
            JSONObject supplierOverrideComment = new JSONObject();
            supplierOverrideComment.put("supplierOverrideComment", row.get("bd_supplier_override_reason"));
            commentsArray.put(supplierOverrideComment);
        }

        Object appOverridden = row.get("bd_app");
        Object originalApp = row.get("app");

        if (appOverridden != null && appOverridden != originalApp) {
            Integer proposedApp = (Integer) appOverridden;
            overriddenValues.put("app", proposedApp);
            JSONObject appOverrideComment = new JSONObject();
            appOverrideComment.put("appOverrideComment", row.get("bd_app_override_reason"));
            commentsArray.put(appOverrideComment);
        }

        Object slaOverridden = row.get("new_sla");
        Object originalSla = row.get("sla");

        if (slaOverridden != null && originalSla != slaOverridden) {
            Integer proposedSla = (Integer) slaOverridden;
            overriddenValues.put("sla", proposedSla);
            JSONObject slaOverrideComment = new JSONObject();
            slaOverrideComment.put("slaOverrideComment", row.get("bd_sla_override_reason"));
            commentsArray.put(slaOverrideComment);
        }
        overriddenValues.put("overrideComment", commentsArray);
        return overriddenValues;
    }


}
