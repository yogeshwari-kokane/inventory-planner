package fk.retail.ip.requirement.internal.command.upload;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.command.CalculateRequirementCommand;
import fk.retail.ip.requirement.internal.command.FdpRequirementIngestorImpl;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.OverrideKey;
import fk.retail.ip.requirement.internal.enums.OverrideStatus;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.ssl.client.SslClient;
import fk.retail.ip.ssl.model.SupplierSelectionRequest;
import fk.retail.ip.ssl.model.SupplierSelectionResponse;
import fk.retail.ip.ssl.model.SupplierView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by agarwal.vaibhav on 06/02/17.
 */

@Slf4j
public class CDOReviewUploadCommand extends UploadCommand {

    private final Provider<CalculateRequirementCommand> calculateRequirementCommandProvider;
    private final SslClient sslClient;

    @Inject
    public CDOReviewUploadCommand(RequirementRepository requirementRepository, FdpRequirementIngestorImpl fdpRequirementIngestor,
                                  Provider<CalculateRequirementCommand> calculateRequirementCommandProvider, SslClient sslClient) {
        super(requirementRepository, fdpRequirementIngestor);
        this.calculateRequirementCommandProvider = calculateRequirementCommandProvider;
        this.sslClient = sslClient;
    }

    @Override
    Map<String, Object> validateAndSetStateSpecificFields(RequirementDownloadLineItem requirementDownloadLineItem, Requirement requirement,
                                                          Map<String, String> fsnToVerticalMap, MultiKeyMap<String,SupplierSelectionResponse> fsnWhSupplierMap) {
        String supplierOverrideComment = requirementDownloadLineItem.getCdoSupplierOverrideReason();
        Integer bdProposedQuantity = requirementDownloadLineItem.getCdoQuantityOverride();
        Integer bdProposedSla = requirementDownloadLineItem.getNewSla();
        Integer bdProposedApp = requirementDownloadLineItem.getCdoPriceOverride();
        String bdProposedSupplier = requirementDownloadLineItem.getCdoSupplierOverride();
        String currentSupplier = requirementDownloadLineItem.getSupplier();
        Integer currentQuantity =  requirementDownloadLineItem.getQuantity();
        Integer currentApp = requirementDownloadLineItem.getApp();
        Integer currentSla = requirementDownloadLineItem.getSla();
        String quantityOverrideComment = requirementDownloadLineItem.getCdoQuantityOverrideReason();
        String appOverrideComment = requirementDownloadLineItem.getCdoPriceOverrideReason();
        Map<String, Object> overriddenValues = new HashMap<>();

        String validationComment = "";
        SupplierView supplierView = null;

        Optional<String> validationResponse = validateQuantityOverride(
                currentQuantity,
                bdProposedQuantity,
                quantityOverrideComment
        );

        if (validationResponse.isPresent()) {
            validationComment = convertToLineSeparatedComment(validationComment, validationResponse.get());
        }

        validationResponse = validateSupplierOverride(bdProposedSupplier, currentSupplier, supplierOverrideComment);
        if (validationResponse.isPresent()) {
            validationComment = convertToLineSeparatedComment(validationComment, validationResponse.get());
        }
        else if (!isEmptyString(bdProposedSupplier)) {
                supplierView = validateOverriddenSupplierFound(bdProposedSupplier, requirement, fsnWhSupplierMap);
                if (supplierView == null) {
                    validationComment = convertToLineSeparatedComment(validationComment, Constants.SUPPLIER_NOT_FOUND.toString());
                }
        }

        validationResponse = validateSlaOverride(bdProposedSla);
        if (validationResponse.isPresent()) {
            validationComment = convertToLineSeparatedComment(validationComment, validationResponse.get());
        }

        validationResponse = validateAppOverride(bdProposedApp, currentApp, appOverrideComment);
        if (validationResponse.isPresent()) {
            validationComment = convertToLineSeparatedComment(validationComment, validationResponse.get());
        }

        if (!validationComment.isEmpty()) {
            overriddenValues.put(Constants.STATUS, OverrideStatus.FAILURE.toString());
            overriddenValues.put(OverrideKey.OVERRIDE_COMMENT.toString(), validationComment);
            return overriddenValues;
        }

        overriddenValues = getOverriddenFields(
                currentQuantity,
                currentSupplier,
                currentApp,
                currentSla,
                bdProposedQuantity,
                bdProposedApp,
                bdProposedSla,
                bdProposedSupplier,
                quantityOverrideComment,
                appOverrideComment,
                supplierOverrideComment,
                supplierView,
                requirement,
                fsnToVerticalMap
        );

        return overriddenValues;
    }

    private Optional<String> validateAppOverride(Integer bdProposedApp, Integer currentApp, String appOverrideComment) {
        String validationComment;
        if (bdProposedApp == null) {
            return Optional.empty();
        }
        if (bdProposedApp <= 0) {
            validationComment = isEmptyString(appOverrideComment) ?
                    Constants.INVALID_APP_WITHOUT_COMMENT :
                    Constants.APP_QUANTITY_IS_NOT_GREATER_THAN_ZERO;
            return Optional.of(validationComment);
        } else if(bdProposedApp != currentApp && isEmptyString(appOverrideComment)) {
            validationComment = Constants.APP_OVERRIDE_COMMENT_IS_MISSING;
            return Optional.of(validationComment);
        } else {
            return Optional.empty();
        }

    }

    private Optional<String> validateSupplierOverride(
            String bdProposedSupplier,
            String currentSupplier,
            String supplierOverrideComment
    ) {
        String validationComment;

        if (isEmptyString(bdProposedSupplier)) {
            return Optional.empty();
        }
        if (bdProposedSupplier != currentSupplier && isEmptyString(supplierOverrideComment)) {
            validationComment = Constants.SUPPLIER_OVERRIDE_COMMENT_IS_MISSING;
            return Optional.of(validationComment);
        }
        return Optional.empty();
    }

    private SupplierView validateOverriddenSupplierFound(String supplierName, Requirement requirement, MultiKeyMap<String,SupplierSelectionResponse> fsnWhSupplierMap) {
        SupplierSelectionResponse supplierSelectionResponse = fsnWhSupplierMap.get(requirement.getFsn(), requirement.getWarehouse());
        if (supplierSelectionResponse == null)
            return null;
        if(supplierSelectionResponse.getSuppliers()!=null) {
            Optional<SupplierView> supplier =
                    supplierSelectionResponse.getSuppliers().stream().filter(s -> (s.getSourceId().equals(supplierName))).findFirst();
            if (supplier.isPresent())
                return supplier.get();
        }
        if(supplierSelectionResponse.getOtherSuppliers()!=null) {
            Optional<SupplierView> otherSupplier =
                    supplierSelectionResponse.getOtherSuppliers().stream().filter(s -> (s.getSourceId().equals(supplierName))).findFirst();
            if (otherSupplier.isPresent())
                return otherSupplier.get();
        }
        return null;
    }

    private Optional<String> validateSlaOverride(Integer bdProposedSla) {
        String validationComment;
        if (bdProposedSla == null) {
            return Optional.empty();
        }

        if (bdProposedSla <= 0){
            validationComment = Constants.SLA_QUANTITY_IS_NOT_GREATER_THAN_ZERO;
            return Optional.of(validationComment);
        }
        return Optional.empty();
    }

    private Map<String, Object> getOverriddenFields(
            Integer currentQuantity,
            String currentSupplier,
            Integer currentApp,
            Integer currentSla,
            Integer bdProposedQuantity,
            Integer bdProposedApp,
            Integer bdProposedSla,
            String bdProposedSupplier,
            String quantityOverrideComment,
            String appOverrideComment,
            String supplierOverrideComment,
            SupplierView supplierView,
            Requirement requirement,
            Map<String, String> fsnToVerticalMap
    ) {

        Map<String, Object> overriddenValues = new HashMap<>();
        JSONObject overrideComment = new JSONObject();
        overriddenValues.put(Constants.STATUS, OverrideStatus.SUCCESS.toString());


        if (bdProposedQuantity != null && bdProposedQuantity != currentQuantity) {
            Integer quantityToUse = bdProposedQuantity;
            overriddenValues.put(OverrideKey.QUANTITY.toString(), quantityToUse);
            overrideComment.put(Constants.QUANTITY_OVERRIDE_COMMENT, quantityOverrideComment);
            overriddenValues.put(Constants.STATUS, OverrideStatus.UPDATE.toString());
        }

        if (!isEmptyString(bdProposedSupplier) && bdProposedSupplier != currentSupplier && supplierView!=null) {
            overriddenValues.put(OverrideKey.SUPPLIER.toString(),supplierView);
            overrideComment.put(Constants.SUPPLIER_OVERRIDE_COMMENT, supplierOverrideComment);
            overriddenValues.put(Constants.STATUS, OverrideStatus.UPDATE.toString());
        }

        if (bdProposedApp != null && bdProposedApp != currentApp && (isEmptyString(bdProposedSupplier) ||
                (bdProposedSupplier != currentSupplier && supplierView!=null))) {
            Integer appToUse = bdProposedApp;
            overriddenValues.put(OverrideKey.APP.toString(), appToUse);
            overrideComment.put(Constants.APP_OVERRIDE_COMMENT, appOverrideComment);
            overriddenValues.put(Constants.STATUS, OverrideStatus.UPDATE.toString());
        }
        else if(overriddenValues.containsKey(OverrideKey.SUPPLIER.toString()))
        {
            Integer appToUse = supplierView.getApp();
            overriddenValues.put(OverrideKey.APP.toString(), appToUse);
            overrideComment.put(Constants.APP_OVERRIDE_COMMENT, Constants.DEFAULT_APP_OVERRIDE_COMMENT.toString());
            overriddenValues.put(Constants.STATUS, OverrideStatus.UPDATE.toString());
        }

        if (bdProposedSla != null && bdProposedSla != currentSla && (isEmptyString(bdProposedSupplier) ||
                (bdProposedSupplier != currentSupplier && supplierView!=null))) {
            Integer slaToUse = bdProposedSla;
            overriddenValues.put(OverrideKey.SLA.toString(), slaToUse);
            overriddenValues.put(Constants.STATUS, OverrideStatus.UPDATE.toString());
        }
        else if(overriddenValues.containsKey(OverrideKey.SUPPLIER.toString()))
        {
            Integer slaToUse = calculateRequirementCommandProvider.get().getSla
                (fsnToVerticalMap.get(requirement.getFsn()), requirement.getWarehouse(), supplierView.getSourceId(),
                        supplierView.getSla());
            overriddenValues.put(OverrideKey.SLA.toString(), slaToUse);
            overriddenValues.put(Constants.STATUS, OverrideStatus.UPDATE.toString());
        }

        overriddenValues.put(OverrideKey.OVERRIDE_COMMENT.toString(), overrideComment);

        return overriddenValues;
    }

    private String convertToLineSeparatedComment(String firstString, String secondString) {
        return firstString.isEmpty() ? secondString : firstString + System.lineSeparator() + secondString;
    }


}
