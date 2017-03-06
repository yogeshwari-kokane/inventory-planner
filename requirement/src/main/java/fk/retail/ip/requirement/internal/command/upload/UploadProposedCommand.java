package fk.retail.ip.requirement.internal.command.upload;

import com.google.inject.Inject;
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
public class UploadProposedCommand extends UploadCommand {

    @Inject
    public UploadProposedCommand(RequirementRepository requirementRepository) {
        super(requirementRepository);
    }

    @Override
    public Map<String, Object> validateAndSetStateSpecific(RequirementDownloadLineItem requirementDownloadLineItem) {
        Integer currentQuantity = requirementDownloadLineItem.getQuantity();
        Integer proposedQuantity = requirementDownloadLineItem.getIpcQuantityOverride();
        String quantityOverrideComment = requirementDownloadLineItem.getIpcQuantityOverrideReason();
        Map<String, Object> overriddenFields = new HashMap<>();

        String validationComment = isQuantityOverrideValid(currentQuantity, proposedQuantity, quantityOverrideComment);
        System.out.println("validation comment is " + validationComment);
        if (validationComment.isEmpty()) {
//            System.out.println("it is valid override");
            overriddenFields = getOverriddenFields(currentQuantity, proposedQuantity, quantityOverrideComment);
        } else {
            log.debug("invalid override to requirement");
            overriddenFields.put(OverrideKeys.STATUS.toString(), OverrideKeys.FAILURE.toString());
            overriddenFields.put(OverrideKeys.OVERRIDE_COMMENT.toString(), validationComment);
        }
        return overriddenFields;
    }

//    private Map<String, Object> isValidOverrideQuantity(Object currentQuantity, Object proposedQuantity, String quantityOverrideComment) {
//        Map<String, Object> validOverride = new HashMap<>();
//
//        if (proposedQuantity == null) {
////            System.out.println("proposed quantity is null");
//            return validOverride;
//        }
////        System.out.println("proposed quantity is not null");
////        System.out.println("proposed quantity is  " + proposedQuantity);
////        System.out.println("current quantity is " + currentQuantity);
////        System.out.println("quantity override comment is " + quantityOverrideComment);
//
//        if ((proposedQuantity instanceof Integer) && (Integer) proposedQuantity > 0) {
//            if (quantityOverrideComment == null && currentQuantity != proposedQuantity) {
//                log.debug("proposed quantity override reason is missing");
////                System.out.println("quantity override comment is missing");
//                validOverride.put("failure", "quantity override comment is absent");
//            }
//        } else {
//            log.debug("quantity is less than zero or not integer");
////            System.out.println("quantity is not integer or less than zero");
//            validOverride.put("failure", "quantity is less than zero or not integer");
//
//        }
//        return validOverride;
//    }

    private Map<String, Object> getOverriddenFields(Integer currentQuantity, Integer proposedQuantity, String quantityOverrideComment) {
        Map<String, Object> overriddenValues = new HashMap<>();

        if (proposedQuantity != null && proposedQuantity != currentQuantity) {
            overriddenValues.put(OverrideKeys.QUANTITY.toString(), proposedQuantity);
            JSONObject comment = new JSONObject();
            comment.put("quantityOverrideComment", quantityOverrideComment);
            overriddenValues.put("overrideComment", comment);
            overriddenValues.put(OverrideKeys.STATUS.toString(), OverrideKeys.UPDATE.toString());
        } else {
            overriddenValues.put(OverrideKeys.STATUS.toString(), OverrideKeys.SUCCESS.toString());
        }
        return overriddenValues;
    }

}
