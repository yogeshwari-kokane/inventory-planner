package fk.retail.ip.requirement.internal.command.upload;


import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.OverrideKeys;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.RequirementUploadLineItem;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vaibhav.agarwal on 03/02/17.
 */
@Slf4j
public abstract class UploadCommand {

    abstract Map<String, Object> validateAndSetStateSpecific(RequirementDownloadLineItem row);

    private final RequirementRepository requirementRepository;

    public UploadCommand(RequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    public List<RequirementUploadLineItem> execute(List<RequirementDownloadLineItem> requirementDownloadLineItems, List<Requirement> requirements) {

        /*TODO: Check if the key has to be fsn-fc pair or requirement id*/
//        Map<Pair<String,String>,Requirement> requirementMap1 = requirements.stream().collect(Collectors.toMap(requirement -> {
//            return new ImmutablePair<String, String>(requirement.getFsn(), requirement.getWarehouse());
//        }, requirement -> requirement));

        Map<Long, Requirement> requirementMap = requirements.stream().collect(Collectors.toMap(requirement -> {
            return requirement.getProjectionId();
        }, requirement -> requirement));

        ArrayList<RequirementUploadLineItem> requirementUploadLineItems = new ArrayList<>();
        int rowCount = 0;
        for(RequirementDownloadLineItem row : requirementDownloadLineItems) {
            RequirementUploadLineItem requirementUploadLineItem = new RequirementUploadLineItem();
            rowCount += 1;
            String fsn = row.getFsn();
            String warehouse = row.getWarehouseName();

            String genericComment = validateGenericColumns(fsn, warehouse);

            /*TODO : remove null checks*/
            if (!genericComment.isEmpty()) {
                requirementUploadLineItem.setFailureReason(genericComment);
                if (fsn != null) {
                    requirementUploadLineItem.setFsn(fsn);
                } else {
                    requirementUploadLineItem.setFsn("");
                }

                if (warehouse != null) {
                    requirementUploadLineItem.setWarehouse(warehouse);
                } else {
                    requirementUploadLineItem.setWarehouse("");
                }

                requirementUploadLineItem.setRowNumber(rowCount);
                requirementUploadLineItems.add(requirementUploadLineItem);
                continue;
            }

            Map<String, Object> overriddenValues = validateAndSetStateSpecific(row);
            String overrideStatus = overriddenValues.get(OverrideKeys.STATUS.toString()).toString();

            if (overrideStatus == OverrideKeys.FAILURE.toString()) {
                requirementUploadLineItem.setFailureReason(overriddenValues.get
                        (OverrideKeys.OVERRIDE_COMMENT.toString()).toString());
                requirementUploadLineItem.setFsn(fsn);
                requirementUploadLineItem.setRowNumber(rowCount);
                requirementUploadLineItem.setWarehouse(warehouse);
                requirementUploadLineItems.add(requirementUploadLineItem);

            } else if (overrideStatus == OverrideKeys.UPDATE.toString()){
//                Pair<String, String> fsn_warehouse_pair = new ImmutablePair<>(fsn, warehouse);
                Long requirementId = row.getRequirementId();

                if (requirementMap.containsKey(requirementId)) {
                    Requirement requirement = requirementMap.get(requirementId);

                    if (overriddenValues.containsKey(OverrideKeys.QUANTITY.toString())) {
                        requirement.setQuantity((Integer) overriddenValues.get(OverrideKeys.QUANTITY.toString()));
                    }

                    if (overriddenValues.containsKey(OverrideKeys.SLA.toString())) {
                        requirement.setSla((Integer) overriddenValues.get(OverrideKeys.SLA.toString()));
                    }

                    if (overriddenValues.containsKey(OverrideKeys.APP.toString())) {
                        requirement.setApp((Integer) overriddenValues.get(OverrideKeys.APP.toString()));
                    }

                    if (overriddenValues.containsKey(OverrideKeys.SUPPLIER.toString())) {
                        requirement.setSupplier(overriddenValues.get(OverrideKeys.SUPPLIER.toString()).toString());
                    }

                    if (overriddenValues.containsKey(OverrideKeys.OVERRIDE_COMMENT.toString())) {
                        System.out.println("comment to be : " + overriddenValues.get("overrideComment"));
                        requirement.setOverrideComment(overriddenValues.get(OverrideKeys.OVERRIDE_COMMENT.toString()).toString());
                    }

                    requirementRepository.persist(requirement);

                } else {

                    requirementUploadLineItem.setFailureReason(Constants.REQUIREMENT_NOT_FOUND_FOR_GIVEN_REQUIREMENT_ID);
                    requirementUploadLineItem.setFsn(fsn);
                    requirementUploadLineItem.setRowNumber(rowCount);
                    requirementUploadLineItem.setWarehouse(warehouse);
                    requirementUploadLineItems.add(requirementUploadLineItem);
                }

            }

        }
        return requirementUploadLineItems;
    }

    private String validateGenericColumns(String fsn, String warehouse) {
        String genericValidationComment = new String();
        if (fsn == null || warehouse == null) {
            log.debug(Constants.FSN_OR_WAREHOUSE_IS_MISSING);
           genericValidationComment =  Constants.FSN_OR_WAREHOUSE_IS_MISSING;
        }
        return genericValidationComment;
    }

    protected String isQuantityOverrideValid(Integer currentQuantity, Integer suggestedQuantity, String overrideComment) {
        String validationComment = new String();
        if (suggestedQuantity != null) {
            if (suggestedQuantity <= 0) {

                if (isEmptyString(overrideComment)) {
                    validationComment = Constants.QUANTITY_OVERRIDE_IS_NOT_GREATER_THAN_ZERO_AND_COMMENT_IS_MISSING.toString();
                } else {
                    validationComment = Constants.SUGGESTED_QUANTITY_IS_NOT_GREATER_THAN_ZERO.toString();
                }
                log.debug(validationComment);

            } else if (suggestedQuantity != currentQuantity && isEmptyString(overrideComment)) {

                validationComment = Constants.QUANTITY_OVERRIDE_COMMENT_IS_MISSING.toString();
                log.debug(validationComment);
            }
        }

        return validationComment;
    }

    protected boolean isEmptyString(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

}
