package fk.retail.ip.requirement.internal.context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.command.PayloadCreationHelper;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.FdpRequirementEventType;
import fk.retail.ip.requirement.internal.enums.OverrideKey;
import fk.retail.ip.requirement.internal.enums.PolicyType;
import fk.retail.ip.requirement.model.RequirementChangeMap;
import fk.retail.ip.requirement.model.RequirementChangeRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Created by yogeshwari.k on 25/04/17.
 */
@Slf4j
public class MinMaxApplicator extends PolicyApplicator {

    public MinMaxApplicator(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void applyPolicies(String fsn, List<Requirement> requirements, Map<PolicyType, String> policyTypeMap,
                              ForecastContext forecastContext, OnHandQuantityContext onHandQuantityContext,
                              List<RequirementChangeRequest> requirementChangeRequestList) {
        Map<String, Double> warehouseToMinMap = parseMinMax(policyTypeMap.get(PolicyType.MIN));
        Map<String, Double> warehouseToMaxMap = parseMinMax(policyTypeMap.get(PolicyType.MAX));
        requirements.stream().filter(requirement -> !Constants.ERROR_STATE.equals(requirement.getState())).forEach(requirement -> {
            String warehouse = requirement.getWarehouse();
            Double minUnits = warehouseToMinMap.get(warehouse);
            if (!isValidMinMax(minUnits)) {
                //min policy not found
                markAsError(requirement, String.format(Constants.VALID_POLICY_NOT_FOUND, PolicyType.MIN));
                return;
            }
            addToSnapshot(requirement, PolicyType.MIN, minUnits);
            double onHandQuantity = onHandQuantityContext.getTotalQuantity(fsn, warehouse);
            if (onHandQuantity <= minUnits) {
                //reorder point has been reached
                Double maxUnits = warehouseToMaxMap.get(warehouse);
                if (!isValidMinMax(maxUnits) || maxUnits < minUnits) {
                    //max policy not found
                    markAsError(requirement, String.format(Constants.VALID_POLICY_NOT_FOUND, PolicyType.MAX));
                    return;
                }
                addToSnapshot(requirement, PolicyType.MAX, maxUnits);
                requirement.setQuantity(maxUnits - onHandQuantity);
                //Add MIN_MAX_QUANTITY events to fdp request
                log.info("Adding MIN_MAX_QUANTITY events to fdp request");
                RequirementChangeRequest requirementChangeRequest = new RequirementChangeRequest();
                List<RequirementChangeMap> requirementChangeMaps = Lists.newArrayList();
                requirementChangeMaps.add(PayloadCreationHelper.createChangeMap(OverrideKey.QUANTITY.toString(), null,
                        String.valueOf(requirement.getQuantity()), FdpRequirementEventType.MIN_MAX_QUANTITY.toString(),
                        "MIN MAX policies applied", "system"));
                requirementChangeRequest.setRequirement(requirement);
                requirementChangeRequest.setRequirementChangeMaps(requirementChangeMaps);
                requirementChangeRequestList.add(requirementChangeRequest);
            }
        });
    }

    private Map<String, Double> parseMinMax(String value) {
        Map<String, Double> policyMap = Maps.newHashMap();
        TypeReference<Map<String, Map<String, Double>>> typeReference = new TypeReference<Map<String, Map<String, Double>>>() {};
        Map<String, Map<String, Double>> rawMap = super.parsePolicy(value, typeReference);
        if (rawMap != null) {
            rawMap.entrySet().stream().forEach(entry -> policyMap.put(entry.getKey(), entry.getValue().get("units")));
        }
        return policyMap;
    }

    public boolean isValidMinMax(Double value) {
        if (value == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidRopRoc(Double ropDays, Double rocDays) {
        if (ropDays == null || rocDays == null) {
            return false;
        } else {
            return true;
        }
    }

}
