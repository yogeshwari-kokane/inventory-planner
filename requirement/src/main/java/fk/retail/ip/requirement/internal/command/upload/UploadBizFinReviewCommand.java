package fk.retail.ip.requirement.internal.command.upload;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by agarwal.vaibhav on 06/02/17.
 */
public class UploadBizFinReviewCommand extends UploadCommand {
    @Override
    Map<String, Object> validateAndSetStateSpecific(Map<String, Object> row) {

        String quantityOverrideComment = row.get("bizfin_comments").toString();
        Object currentQuantity = row.get("quantity");
        Object bizfinProposedQuantity = row.get("bizfin_quantity");
        Map<String, Object> overriddenValues;
        overriddenValues = isValidOverrideQuantity(bizfinProposedQuantity, currentQuantity, quantityOverrideComment);
        if (overriddenValues.isEmpty()) {
            overriddenValues = getOverriddenFields(bizfinProposedQuantity, currentQuantity, quantityOverrideComment);
        }
        return overriddenValues;
    }

    private Map<String, Object> getOverriddenFields(Object bizfinProposedQuantity, Object currentQuantity, String quantityOverrideComment) {
        Map<String, Object> overriddenValues = new HashMap<>();
        JSONArray commentsArray = new JSONArray();

        if (bizfinProposedQuantity != null) {
            Integer quantityToUse = (Integer) bizfinProposedQuantity;
            overriddenValues.put("quantity", quantityToUse);
            JSONObject quantityOverrideJson = new JSONObject();
            quantityOverrideJson.put("quantityOverrideComment", quantityOverrideComment);
            commentsArray.put(quantityOverrideJson);
            overriddenValues.put("overrideComment", commentsArray);
        }
        return overriddenValues;
    }

    private Map<String, Object> isValidOverrideQuantity(Object proposedQuantity, Object stateQuantity, String quantityOverrideComment) {
        Map<String, Object> validOverride = new HashMap<>();
        if ((stateQuantity instanceof Integer) && (Integer) stateQuantity > 0) {
            if (quantityOverrideComment.isEmpty() && stateQuantity != proposedQuantity) {
                //log => comment is absent
                validOverride.put("failure", "quantity override comment is absent");
            }
        } else {
            //log => quantity is less than zero or not integer
            validOverride.put("failure", "quantity is less than zero or not integer");

        }
        return validOverride;
    }
}
