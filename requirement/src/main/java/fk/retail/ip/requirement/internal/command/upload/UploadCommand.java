package fk.retail.ip.requirement.internal.command.upload;


import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.RequirementUploadLineItem;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.ws.rs.core.StreamingOutput;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vaibhav.agarwal on 03/02/17.
 */
public abstract class UploadCommand {

    private List<Requirement> requirements;

    abstract String validateStateSpecific(Map<String, Object> row);
    abstract Map<String, Object> getOverriddenFields(Map<String, Object> row);

    public List<RequirementUploadLineItem> execute(List<Map<String, Object>> parsedJson, List<Requirement> requirements) {

        Map<Pair<String,String>,Requirement> requirementMap = requirements.stream().collect(Collectors.toMap(requirement -> {
            return new ImmutablePair<String, String>(requirement.getFsn(), requirement.getWarehouse());
        }, requirement -> requirement));

        ArrayList<RequirementUploadLineItem> requirementUploadLineItems = new ArrayList<>();
        for(Map<String, Object> row : parsedJson) {
            RequirementUploadLineItem requirementUploadLineItem = new RequirementUploadLineItem();
            String rowId = row.get("id1").toString();
            String fsn = row.get("fsn").toString();
            String warehouse = row.get("warehouse").toString();
            String genericComment = validateGenericRowColumns(row);
            String stateSpecificComment = validateStateSpecific(row);

            if (genericComment != null) {
                requirementUploadLineItem.setFailureReason(genericComment);
                requirementUploadLineItem.setFsn(fsn);
                requirementUploadLineItem.setRowId(rowId);
                requirementUploadLineItems.add(requirementUploadLineItem);
                continue;
            }
            if (stateSpecificComment != null) {
                requirementUploadLineItem.setFailureReason(stateSpecificComment);
                requirementUploadLineItem.setFsn(fsn);
                requirementUploadLineItem.setRowId(rowId);
                requirementUploadLineItems.add(requirementUploadLineItem);
                continue;
            }

            Map<String, Object> overriddenValues = getOverriddenFields(row);
            Requirement requirement = requirementMap.get(new ImmutablePair<String, String>(fsn, warehouse));
            if (overriddenValues.containsKey("quantity")) {
                requirement.setQuantity((Integer) overriddenValues.get("quantity"));
            }

            if (overriddenValues.containsKey("sla")) {
                requirement.setSla((Integer) overriddenValues.get("sla"));
            }

            if (overriddenValues.containsKey("app")) {
                requirement.setApp((Integer) overriddenValues.get("app"));
            }

            if (overriddenValues.containsKey("supplier")) {
                requirement.setSupplier(overriddenValues.get("supplier").toString());
            }

            if (overriddenValues.containsKey("overrideComment")) {
                requirement.setOverrideComment(overriddenValues.get("overrideComment").toString());
            }
        }
        return requirementUploadLineItems;
    }

    private String validateGenericRowColumns(Map<String, Object> row) {
        String fsn = (String) row.get("fsn");
        String warehouse = (String) row.get("warehouse");
        Integer rowID = (Integer) row.get("id1");
        if (fsn.isEmpty() || warehouse.isEmpty() || rowID == null) {
            //log => id1 ,fsn, and/or warehouse is missing
            return "id1, fsn, and/or warehouse is missing";
        }else {
            return null;
        }
    }

}
