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
public class UploadBizFinReviewCommand extends UploadCommand {

    @Inject
    public UploadBizFinReviewCommand(RequirementRepository requirementRepository) {
        super(requirementRepository);
    }

    @Override
    Map<String, Object> validateAndSetStateSpecific(RequirementDownloadLineItem requirementDownloadLineItem) {

        String quantityOverrideComment = requirementDownloadLineItem.getBizFinComment();
        Object currentQuantity = requirementDownloadLineItem.getQuantity();
        Object bizfinProposedQuantity = requirementDownloadLineItem.getBizFinRecommendedQuantity();
        Map<String, Object> overriddenValues;
        overriddenValues = isValidOverrideQuantity(bizfinProposedQuantity, currentQuantity, quantityOverrideComment);
        if (overriddenValues.isEmpty()) {
            overriddenValues = getOverriddenFields(bizfinProposedQuantity, quantityOverrideComment);
        } else {
            log.debug("invalid override");
        }
        return overriddenValues;
    }

    private Map<String, Object> getOverriddenFields(Object bizfinProposedQuantity, String quantityOverrideComment) {
        Map<String, Object> overriddenValues = new HashMap<>();

        if (bizfinProposedQuantity != null) {
            Integer quantityToUse = (Integer) bizfinProposedQuantity;
            overriddenValues.put("quantity", quantityToUse);
            JSONObject quantityOverrideJson = new JSONObject();
            quantityOverrideJson.put("quantityOverrideComment", quantityOverrideComment);
            overriddenValues.put("overrideComment", quantityOverrideJson);
        } else {
            if (!quantityOverrideComment.isEmpty()) {
                JSONObject commentOverridden = new JSONObject();
                commentOverridden.put("quantityOverrideComment", quantityOverrideComment);
                overriddenValues.put("overrideComment", commentOverridden);
            }
        }
        return overriddenValues;
    }

    private Map<String, Object> isValidOverrideQuantity(Object proposedQuantity, Object stateQuantity, String quantityOverrideComment) {
        Map<String, Object> validOverride = new HashMap<>();

        if (stateQuantity == null) {
            return validOverride;
        }

        if ((stateQuantity instanceof Integer) && (Integer) stateQuantity > 0) {
            if (quantityOverrideComment == null && stateQuantity != proposedQuantity) {
                log.debug("bizfin override comment is missing");
                validOverride.put("failure", "quantity override comment is absent");
            }
        } else {
            log.debug("quantity is less than or equal to zero or not integer");
            validOverride.put("failure", "quantity is less than zero or not integer");

        }
        return validOverride;
    }
}
