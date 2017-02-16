package fk.retail.ip.requirement.internal.command.upload;

import com.google.inject.Inject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by agarwal.vaibhav on 06/02/17.
 */
public class UploadProposedCommand extends UploadCommand {

    @Override
    public Map<String, Object> validateAndSetStateSpecific(Map<String, Object> row) {
        Object currentQuantity = row.get("ipc_qty");
        Object proposedQuantity = row.get("quantity");
        String quantityOverrideComment = (String) row.get("ipc_qty_override_reason");
        Map<String, Object> overriddenFields;
        overriddenFields = isValidOverrideQuantity(currentQuantity, proposedQuantity, quantityOverrideComment);
        if (!overriddenFields.isEmpty()) {
            overriddenFields = getOverriddenFields(currentQuantity, proposedQuantity, quantityOverrideComment);
        }
        return overriddenFields;
    }

    private Map<String, Object> isValidOverrideQuantity(Object stateQuantity, Object proposedQuantity, String quantityOverrideComment) {
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

    private Map<String, Object> getOverriddenFields(Object currentQuantity, Object proposedQuantity, String quantityOverrideComment) {
        Map<String, Object> overriddenValues = new HashMap<>();
        JSONArray commentsArray = new JSONArray();

        if (proposedQuantity != null && proposedQuantity != currentQuantity) {
            Integer quantityToUse = (Integer) proposedQuantity;
            overriddenValues.put("quantity", quantityToUse);
            JSONObject comment = new JSONObject();
            comment.put("quantityOverrideComment", quantityOverrideComment);
            commentsArray.put(comment);
            overriddenValues.put("overrideComment", commentsArray);
        }
        return overriddenValues;
    }

}
