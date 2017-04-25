package fk.retail.ip.requirement.internal.command.upload;


import com.google.common.collect.Lists;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.command.CalculateRequirementCommand;
import fk.retail.ip.requirement.internal.command.FdpRequirementIngestorImpl;
import fk.retail.ip.requirement.internal.command.PayloadCreationHelper;
import fk.retail.ip.requirement.internal.entities.ProductInfo;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.FdpRequirementEventType;
import fk.retail.ip.requirement.internal.enums.OverrideKey;
import fk.retail.ip.requirement.internal.enums.OverrideStatus;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.RequirementChangeMap;
import fk.retail.ip.requirement.model.RequirementChangeRequest;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.UploadOverrideFailureLineItem;
import fk.retail.ip.ssl.client.SslClient;
import fk.retail.ip.ssl.model.SupplierSelectionRequest;
import fk.retail.ip.ssl.model.SupplierSelectionResponse;
import fk.retail.ip.ssl.model.SupplierView;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
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
    private final FdpRequirementIngestorImpl fdpRequirementIngestor;
    private final Provider<CalculateRequirementCommand> calculateRequirementCommandProvider;
    private final SslClient sslClient;
    private final ProductInfoRepository productInfoRepository;

    public UploadCommand(RequirementRepository requirementRepository, FdpRequirementIngestorImpl fdpRequirementIngestor, Provider<CalculateRequirementCommand> calculateRequirementCommandProvider, SslClient sslClient, ProductInfoRepository productInfoRepository) {
        this.requirementRepository = requirementRepository;
        this.fdpRequirementIngestor = fdpRequirementIngestor;
        this.calculateRequirementCommandProvider = calculateRequirementCommandProvider;
        this.sslClient = sslClient;
        this.productInfoRepository = productInfoRepository;
    }

    public List<UploadOverrideFailureLineItem> execute(
            List<RequirementDownloadLineItem> requirementDownloadLineItems,
            List<Requirement> requirements,
            String userId
    ) {

        Map<Long, Requirement> requirementMap = requirements.stream().
                collect(Collectors.toMap(Requirement::getId, Function.identity()));
        Set<String> fsns = requirements.stream().map(Requirement::getFsn).collect(Collectors.toSet());
        List<ProductInfo> productInfos = productInfoRepository.getProductInfo(fsns);
        Map<String, String> fsnToVerticalMap = productInfos.stream().collect(Collectors.toMap(ProductInfo::getFsn, ProductInfo::getVertical, (k1, k2) -> k1));

        ArrayList<UploadOverrideFailureLineItem> uploadOverrideFailureLineItems = new ArrayList<>();
        int rowCount = 0;
        List<RequirementChangeRequest> requirementChangeRequestList = Lists.newArrayList();
        for(RequirementDownloadLineItem row : requirementDownloadLineItems) {
            UploadOverrideFailureLineItem uploadOverrideFailureLineItem = new UploadOverrideFailureLineItem();
            rowCount += 1;
            String fsn = row.getFsn();
            String warehouse = row.getWarehouseName();

            Optional<String> genericComment = validateGenericColumns(fsn, warehouse);

            if (genericComment.isPresent()) {
                uploadOverrideFailureLineItem.setFailureReason(genericComment.get());
                uploadOverrideFailureLineItem.setFsn(fsn == null ? "" : fsn);
                uploadOverrideFailureLineItem.setWarehouse(warehouse == null ? "" : warehouse);
                uploadOverrideFailureLineItem.setRowNumber(rowCount);
                uploadOverrideFailureLineItems.add(uploadOverrideFailureLineItem);

            } else {

                Map<String, Object> overriddenValues = validateAndSetStateSpecificFields(row);
                String status = overriddenValues.get(Constants.STATUS).toString();
                OverrideStatus overrideStatus = OverrideStatus.fromString(status);

                switch(overrideStatus) {
                    case FAILURE:
                        uploadOverrideFailureLineItem.setFailureReason(overriddenValues.get
                                (OverrideKey.OVERRIDE_COMMENT.toString()).toString());
                        uploadOverrideFailureLineItem.setFsn(fsn);
                        uploadOverrideFailureLineItem.setRowNumber(rowCount);
                        uploadOverrideFailureLineItem.setWarehouse(warehouse);
                        uploadOverrideFailureLineItems.add(uploadOverrideFailureLineItem);
                        break;

                    case UPDATE:
                        Long requirementId = row.getRequirementId();

                        if (requirementMap.containsKey(requirementId)) {
                            //Add IPC_QUANTITY_OVERRIDE, CDO_QUANTITY_OVERRIDE, CDO_APP_OVERRIDE, CDO_SLA_OVERRIDE, CDO_SUPPLIER_OVERRIDE events to fdp request
                            log.info("Adding IPC_QUANTITY_OVERRIDE, CDO_QUANTITY_OVERRIDE, CDO_APP_OVERRIDE, CDO_SLA_OVERRIDE, CDO_SUPPLIER_OVERRIDE events to fdp request");
                            Requirement requirement = requirementMap.get(requirementId);
                            RequirementChangeRequest requirementChangeRequest = new RequirementChangeRequest();
                            List<RequirementChangeMap> requirementChangeMaps = Lists.newArrayList();

                            if (overriddenValues.containsKey(OverrideKey.QUANTITY.toString())) {
                                String eventType = null;
                                String overrideReason = null;
                                if(RequirementApprovalState.PROPOSED.toString().equals(requirement.getState())) {
                                    eventType = FdpRequirementEventType.IPC_QUANTITY_OVERRIDE.toString();
                                    overrideReason = row.getIpcQuantityOverrideReason();
                                }
                                else if(RequirementApprovalState.CDO_REVIEW.toString().equals(requirement.getState())) {
                                    eventType = FdpRequirementEventType.CDO_QUANTITY_OVERRIDE.toString();
                                    overrideReason = row.getCdoQuantityOverrideReason();
                                }
                                if(eventType!=null)
                                    requirementChangeMaps.add(PayloadCreationHelper.createChangeMap(OverrideKey.QUANTITY.toString(), String.valueOf(requirement.getQuantity()), overriddenValues.get(OverrideKey.QUANTITY.toString()).toString(), eventType, overrideReason, userId));

                                requirement.setQuantity
                                        ((Integer) overriddenValues.get(OverrideKey.QUANTITY.toString()));
                            }

                            if (overriddenValues.containsKey(OverrideKey.SLA.toString())) {
                                requirementChangeMaps.add(PayloadCreationHelper.createChangeMap(OverrideKey.SLA.toString(), String.valueOf(requirement.getSla()),row.getNewSla().toString(), FdpRequirementEventType.CDO_SLA_OVERRIDE.toString(), "Sla overridden by CDO", userId));
                                requirement.setSla((Integer) overriddenValues.get(OverrideKey.SLA.toString()));
                            }

                            if (overriddenValues.containsKey(OverrideKey.APP.toString())) {
                                requirementChangeMaps.add(PayloadCreationHelper.createChangeMap(OverrideKey.APP.toString(), String.valueOf(requirement.getApp()),row.getCdoPriceOverride().toString(), FdpRequirementEventType.CDO_APP_OVERRIDE.toString(), row.getCdoPriceOverrideReason(), userId));
                                requirement.setApp((Integer) overriddenValues.get(OverrideKey.APP.toString()));
                            }

                            if (overriddenValues.containsKey(OverrideKey.SUPPLIER.toString())) {
                                SupplierView supplierView = fetchOverriddenSupplier(requirement, overriddenValues.get(OverrideKey.SUPPLIER.toString()).toString());
                                if (supplierView!=null) {
                                    requirementChangeMaps.add(PayloadCreationHelper.createChangeMap(OverrideKey.SUPPLIER.toString(),
                                            String.valueOf(requirement.getSupplier()),row.getCdoSupplierOverride(),
                                            FdpRequirementEventType.CDO_SUPPLIER_OVERRIDE.toString(), row.getCdoSupplierOverrideReason(), userId));
                                    int sla = calculateRequirementCommandProvider.get().getSla
                                            (fsnToVerticalMap.get(requirement.getFsn()), requirement.getWarehouse(), supplierView.getSourceId(),
                                                    supplierView.getSla());
                                    requirement.setSla(sla);
                                    requirement.setMrp(supplierView.getMrp());
                                    requirement.setSupplier
                                        (overriddenValues.get(OverrideKey.SUPPLIER.toString()).toString());
                                }
                            }

                            if (overriddenValues.containsKey(OverrideKey.OVERRIDE_COMMENT.toString())) {
                                requirement.setOverrideComment
                                        (overriddenValues.get(OverrideKey.OVERRIDE_COMMENT.toString()).toString());
                            }

                            if(!requirementChangeMaps.isEmpty()) {
                                requirementChangeRequest.setRequirement(requirement);
                                requirementChangeRequest.setRequirementChangeMaps(requirementChangeMaps);
                                requirementChangeRequestList.add(requirementChangeRequest);
                            }

                            requirement.setUpdatedBy(userId);

                        } else {
                            uploadOverrideFailureLineItem.setFailureReason
                                    (Constants.REQUIREMENT_NOT_FOUND_FOR_GIVEN_REQUIREMENT_ID);
                            uploadOverrideFailureLineItem.setFsn(fsn);
                            uploadOverrideFailureLineItem.setRowNumber(rowCount);
                            uploadOverrideFailureLineItem.setWarehouse(warehouse);
                            uploadOverrideFailureLineItems.add(uploadOverrideFailureLineItem);
                        }
                        break;

                    case SUCCESS:
                        break;

                    }

                }
            }

        //Push IPC_QUANTITY_OVERRIDE, CDO_QUANTITY_OVERRIDE, CDO_APP_OVERRIDE, CDO_SLA_OVERRIDE, CDO_SUPPLIER_OVERRIDE events to fdp
        log.info("Pushing IPC_QUANTITY_OVERRIDE, CDO_QUANTITY_OVERRIDE, CDO_APP_OVERRIDE, CDO_SLA_OVERRIDE, CDO_SUPPLIER_OVERRIDE events to fdp");
        fdpRequirementIngestor.pushToFdp(requirementChangeRequestList);

        return uploadOverrideFailureLineItems;
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
        if (suggestedQuantity < 0) {
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
        return comment == null || comment.trim().isEmpty() ? true : false;
    }

    private SupplierView fetchOverriddenSupplier(Requirement requirement, String supplierName) {
        List<Requirement> requirements = Lists.newArrayList(requirement);
        List<SupplierSelectionRequest> requests = calculateRequirementCommandProvider.get().createSupplierSelectionRequest(requirements);
        List<SupplierSelectionResponse> responses = sslClient.getSupplierSelectionResponse(requests);
        if (requests.size() != responses.size()) {
            return null;
        }
        SupplierSelectionResponse supplierSelectionResponse = responses.get(0);
        Optional<SupplierView> supplier =
                supplierSelectionResponse.getSuppliers().stream().filter(s -> (s.getFullName().equals(supplierName) || s.getName().equals(supplierName))).findFirst();
        if(supplier.isPresent())
            return supplier.get();
        Optional<SupplierView> otherSupplier =
                supplierSelectionResponse.getOtherSuppliers().stream().filter(s -> (s.getFullName().equals(supplierName) || s.getName().equals(supplierName))).findFirst();
        if(otherSupplier.isPresent())
            return supplier.get();
        return null;
    }

}
