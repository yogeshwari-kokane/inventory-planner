package fk.retail.ip.requirement.internal.command.upload;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.Constants1;
import fk.retail.ip.requirement.internal.enums.OverrideKeys;
import fk.retail.ip.requirement.internal.enums.OverrideStatus;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by agarwal.vaibhav on 06/02/17.
 */
@Slf4j
public class ProposedUploadCommand extends UploadCommand {

    @Inject
    public ProposedUploadCommand(RequirementRepository requirementRepository) {
        super(requirementRepository);
    }

    @Override
    public Map<String, Object> validateAndSetStateSpecificFields(RequirementDownloadLineItem requirementDownloadLineItem) {
        Integer currentQuantity = requirementDownloadLineItem.getQuantity();
        Integer proposedQuantity = requirementDownloadLineItem.getIpcQuantityOverride();
        String quantityOverrideComment = requirementDownloadLineItem.getIpcQuantityOverrideReason();
        Map<String, Object> overriddenFields = new HashMap<>();

        Optional<String> validationResponse = validateQuantityOverride(currentQuantity, proposedQuantity, quantityOverrideComment);
        if (validationResponse.isPresent()) {
            String validationComment = validationResponse.get();
            overriddenFields.put(Constants1.getKey(Constants1.STATUS), OverrideStatus.FAILURE.toString());
            overriddenFields.put(OverrideKeys.OVERRIDE_COMMENT.toString(), validationComment);
        } else {
            overriddenFields = getOverriddenFields(currentQuantity, proposedQuantity, quantityOverrideComment);
        }

//        if (validationComment.isPresent()) {
////            System.out.println("it is valid override");
//            overriddenFields = getOverriddenFields(currentQuantity, proposedQuantity, quantityOverrideComment);
//        } else {
//            log.debug("invalid override to requirement");
//            overriddenFields.put(OverrideKeys.STATUS.toString(), OverrideKeys.FAILURE.toString());
//            overriddenFields.put(OverrideKeys.OVERRIDE_COMMENT.toString(), validationComment);
//        }
        return overriddenFields;
    }

    private Map<String, Object> getOverriddenFields(Integer currentQuantity, Integer proposedQuantity, String quantityOverrideComment) {
        Map<String, Object> overriddenValues = new HashMap<>();

        if (proposedQuantity != null && proposedQuantity != currentQuantity) {
            overriddenValues.put(OverrideKeys.QUANTITY.toString(), proposedQuantity);
            JSONObject comment = new JSONObject();
            comment.put(Constants1.getKey(Constants1.QUANTITY_OVERRIDE_COMMENT), quantityOverrideComment);
            overriddenValues.put(OverrideKeys.OVERRIDE_COMMENT.toString(), comment);
            overriddenValues.put(Constants1.getKey(Constants1.STATUS), OverrideStatus.UPDATE.toString());
        } else {
            overriddenValues.put(Constants1.getKey(Constants1.STATUS), OverrideStatus.SUCCESS.toString());
        }
        return overriddenValues;
    }

}
