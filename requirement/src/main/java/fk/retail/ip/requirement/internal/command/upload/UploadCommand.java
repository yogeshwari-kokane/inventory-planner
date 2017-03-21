package fk.retail.ip.requirement.internal.command.upload;


import com.google.common.collect.Lists;
import fk.retail.ip.bigfoot.internal.command.BigfootRequirementIngestor;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.OverrideKeys;
import fk.retail.ip.requirement.internal.enums.OverrideStatus;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.ChangeMap;
import fk.retail.ip.requirement.model.RequirementChangeRequest;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.RequirementUploadLineItem;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by vaibhav.agarwal on 03/02/17.
 * Code never lies.
*/
@Slf4j
public abstract class UploadCommand {

    abstract Map<String, Object> validateAndSetStateSpecificFields(RequirementDownloadLineItem row);

    private final RequirementRepository requirementRepository;

    public UploadCommand(RequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    public List<RequirementUploadLineItem> execute(
            List<RequirementDownloadLineItem> requirementDownloadLineItems, List<Requirement> requirements
    ) {

        Map<Long, Requirement> requirementMap = requirements.stream().
                collect(Collectors.toMap(Requirement::getId, Function.identity()));

        ArrayList<RequirementUploadLineItem> requirementUploadLineItems = new ArrayList<>();
        int rowCount = 0;

        BigfootRequirementIngestor bigfootRequirementIngestor = new BigfootRequirementIngestor();
        List<RequirementChangeRequest> bigfootRequests = Lists.newArrayList();

        for(RequirementDownloadLineItem row : requirementDownloadLineItems) {
            RequirementUploadLineItem requirementUploadLineItem = new RequirementUploadLineItem();
            rowCount += 1;
            String fsn = row.getFsn();
            String warehouse = row.getWarehouseName();

            Optional<String> genericComment = validateGenericColumns(fsn, warehouse);

            if (genericComment.isPresent()) {
                requirementUploadLineItem.setFailureReason(genericComment.get());
                requirementUploadLineItem.setFsn(fsn == null ? "" : fsn);
                requirementUploadLineItem.setWarehouse(warehouse == null ? "" : warehouse);
                requirementUploadLineItem.setRowNumber(rowCount);
                requirementUploadLineItems.add(requirementUploadLineItem);

            } else {

                Map<String, Object> overriddenValues = validateAndSetStateSpecificFields(row);
                String status = overriddenValues.get(Constants.STATUS).toString();
                OverrideStatus overrideStatus = OverrideStatus.fromString(status);

                switch(overrideStatus) {
                    case FAILURE:
                        requirementUploadLineItem.setFailureReason(overriddenValues.get
                                (OverrideKeys.OVERRIDE_COMMENT.toString()).toString());
                        requirementUploadLineItem.setFsn(fsn);
                        requirementUploadLineItem.setRowNumber(rowCount);
                        requirementUploadLineItem.setWarehouse(warehouse);
                        requirementUploadLineItems.add(requirementUploadLineItem);
                        break;

                    case UPDATE:
                        Long requirementId = row.getRequirementId();

                        if (requirementMap.containsKey(requirementId)) {
                            Requirement requirement = requirementMap.get(requirementId);

                            RequirementChangeRequest requirementChangeRequest = new RequirementChangeRequest();
                            List<ChangeMap> changeMaps = Lists.newArrayList();
                            requirementChangeRequest.setRequirement(requirement);

                            if (overriddenValues.containsKey(OverrideKeys.QUANTITY.toString())) {
                                //Add IPC_QUANTITY_OVERRIDE/CDO_QUANTITY_OVERRIDE events to bigfoot request
                                String eventType = null;
                                if(RequirementApprovalState.PROPOSED.toString().equals(requirement.getState()))
                                    eventType = "IPC_QUANTITY_OVERRIDE";
                                else if(RequirementApprovalState.CDO_REVIEW.toString().equals(requirement.getState()))
                                    eventType = "CDO_QUANTITY_OVERRIDE";
                                if (eventType != null) {
                                    changeMaps.add(createChangeMap("Quantity", String.valueOf(requirement.getQuantity()), overriddenValues.get(OverrideKeys.QUANTITY.toString()).toString(), eventType, overriddenValues.get(OverrideKeys.OVERRIDE_COMMENT.toString()).toString(), "dummy_user"));
                                }
                                requirement.setQuantity
                                        ((Integer) overriddenValues.get(OverrideKeys.QUANTITY.toString()));
                            }

                            if (overriddenValues.containsKey(OverrideKeys.SLA.toString())) {
                                //Add CDO_SLA_OVERRIDE events to bigfoot request
                                changeMaps.add(createChangeMap("Sla", requirement.getSla().toString(),overriddenValues.get(OverrideKeys.SLA.toString()).toString(),"CDO_SLA_OVERRIDE", "SLA overridden by CDO", "dummy_user"));
                                requirement.setSla((Integer) overriddenValues.get(OverrideKeys.SLA.toString()));
                            }

                            if (overriddenValues.containsKey(OverrideKeys.APP.toString())) {
                                //Add CDO_APP_OVERRIDE events to bigfoot request
                                changeMaps.add(createChangeMap("App", requirement.getApp().toString(),overriddenValues.get(OverrideKeys.APP.toString()).toString(),"CDO_APP_OVERRIDE", row.getCdoPriceOverrideReason(), "dummy_user"));
                                requirement.setApp((Integer) overriddenValues.get(OverrideKeys.APP.toString()));
                            }

                            if (overriddenValues.containsKey(OverrideKeys.SUPPLIER.toString())) {
                                //Add CDO_SUPPLIER_OVERRIDE events to bigfoot request
                                changeMaps.add(createChangeMap("Supplier", requirement.getSupplier(),overriddenValues.get(OverrideKeys.SUPPLIER.toString()).toString(),"CDO_SUPPLIER_OVERRIDE", row.getCdoSupplierOverrideReason(), "dummy_user"));
                                requirement.setSupplier
                                        (overriddenValues.get(OverrideKeys.SUPPLIER.toString()).toString());
                            }

                            if (overriddenValues.containsKey(OverrideKeys.OVERRIDE_COMMENT.toString())) {
                                requirement.setOverrideComment
                                        (overriddenValues.get(OverrideKeys.OVERRIDE_COMMENT.toString()).toString());
                            }

                            requirementChangeRequest.setChangeMaps(changeMaps);
                            bigfootRequests.add(requirementChangeRequest);

                        } else {
                            requirementUploadLineItem.setFailureReason
                                    (Constants.REQUIREMENT_NOT_FOUND_FOR_GIVEN_REQUIREMENT_ID);
                            requirementUploadLineItem.setFsn(fsn);
                            requirementUploadLineItem.setRowNumber(rowCount);
                            requirementUploadLineItem.setWarehouse(warehouse);
                            requirementUploadLineItems.add(requirementUploadLineItem);
                        }
                        break;

                    case SUCCESS:
                        break;

                    }

                }
            }

        //Push IPC_QUANTITY_OVERRIDE, CDO_QUANTITY_OVERRIDE, CDO_APP_OVERRIDE, CDO_SLA_OVERRIDE, CDO_SUPPLIER_OVERRIDE events to bigfoot
        bigfootRequirementIngestor.pushToBigfoot(bigfootRequests);

        return requirementUploadLineItems;
    }

    private ChangeMap createChangeMap(String attribute, String oldValue, String newValue, String eventType, String reason, String user){
        ChangeMap changeMap = new ChangeMap();
        changeMap.setAttribute(attribute);
        changeMap.setOldValue(oldValue);
        changeMap.setNewValue(newValue);
        changeMap.setEventType(eventType);
        changeMap.setReason(reason);
        changeMap.setUser(user);
        return changeMap;
    }

    private Optional<String> validateGenericColumns(String fsn, String warehouse){
        String genericValidationComment;
        if (fsn == null || warehouse == null) {
           genericValidationComment =  Constants.FSN_OR_WAREHOUSE_IS_MISSING;
            return Optional.of(genericValidationComment);
        }
        return Optional.empty();
    }

    protected Optional<String> validateQuantityOverride(
            Integer currentQuantity, Integer suggestedQuantity, String overrideComment) {
        String validationComment;
        if (suggestedQuantity == null) {
            return Optional.empty();
        }
        if (suggestedQuantity <= 0) {
            validationComment = isEmptyString(overrideComment) ?
                    Constants.INVALID_QUANTITY_WITHOUT_COMMENT :
                    Constants.SUGGESTED_QUANTITY_IS_NOT_GREATER_THAN_ZERO;
            return Optional.of(validationComment);
        } else if (suggestedQuantity != currentQuantity && isEmptyString(overrideComment)) {
            validationComment = Constants.QUANTITY_OVERRIDE_COMMENT_IS_MISSING;
            return Optional.of(validationComment);
        } else {
            return Optional.empty();
        }
    }

    protected boolean isEmptyString(String comment) {
        return true ? comment == null || comment.trim().isEmpty() : false;
    }

}
