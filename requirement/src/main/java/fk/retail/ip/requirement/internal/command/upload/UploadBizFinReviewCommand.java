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
    String validateStateSpecific(Map<String, Object> row) {

        boolean valid = true;
        String quantityOverrideComment = row.get("bizfin_comments").toString();
        Object proposedQuantity = row.get("quantity");
        Object stateQuantity = row.get("bizfin_quantity");
        String comment;
        comment = isValidOverrideQuantity(proposedQuantity, stateQuantity, quantityOverrideComment);
        return comment;
    }

    @Override
    Map<String, Object> getOverriddenFields(Map<String, Object> row) {
        Map<String, Object> overriddenValues = new HashMap<>();
        Object quantityOverridden = row.get("bizfin_quantity");
        JSONArray commentsArray = new JSONArray();

        if (quantityOverridden != null) {
            Integer proposedQuantity = (Integer) quantityOverridden;
            overriddenValues.put("quantity", proposedQuantity);
            JSONObject quantityOverrideComment = new JSONObject();
            quantityOverrideComment.put("quantityOverrideComment", row.get("bizfin_comments"));
            commentsArray.put(quantityOverrideComment);
            overriddenValues.put("overrideComment", commentsArray);
        }
        return overriddenValues;
    }

    String isValidOverrideQuantity(Object proposedQuantity, Object stateQuantity, String quantityOverrideComment) {
        if ((stateQuantity instanceof Integer) && (Integer) stateQuantity > 0) {
            if (quantityOverrideComment.isEmpty() && stateQuantity != proposedQuantity) {
                //log => comment is absent
                return "quantity override comment is absent";
            } else {
                return null;
            }
        } else {
            //log => quantity is less than zero or not integer
            return "quantity is less than zero or not integer";

        }
    }
}
