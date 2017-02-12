package fk.retail.ip.requirement.internal.command.upload;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by agarwal.vaibhav on 06/02/17.
 */
public class UploadProposedCommand extends UploadCommand {

    @Override
    String validateStateSpecific(Map<String, Object> row) {
        Object stateQuantity = row.get("ipc_qty");
        Object proposedQuantity = row.get("quantity");
        String quantityOverrideComment = (String) row.get("ipc_qty_override_reason");
        boolean valid = true;
        String comment;
        comment = isValidOverrideQuantity(stateQuantity, proposedQuantity, quantityOverrideComment);
        return comment;
    }

    private String isValidOverrideQuantity(Object stateQuantity, Object proposedQuantity, String quantityOverrideComment) {
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

    @Override
    Map<String, Object> getOverriddenFields(Map<String, Object> row) {
        Map<String, Object> overriddenValues = new HashMap<>();
        Object quantityOverridden = row.get("ipc_qty");
        Object originalQuantity = row.get("quantity");

        if (quantityOverridden != null && quantityOverridden != originalQuantity) {
            Integer proposedQuantity = (Integer) quantityOverridden;
            overriddenValues.put("quantity", proposedQuantity);
        }
        return overriddenValues;
    }

}
