package fk.retail.ip.requirement.internal.command.upload;

import com.sun.tools.javac.util.Pair;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.RequirementUploadLineItem;

import org.apache.commons.collections4.map.HashedMap;

import javax.ws.rs.core.StreamingOutput;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vaibhav.agarwal on 03/02/17.
 */
public abstract class UploadCommand {

    private List<Requirement> requirements;

    abstract String validateStateSpecific(Map<String, Object> row);
    //abstract Integer quantityOverridden(Integer stateQuantity, Integer rowQuantity);
    //abstract boolean isOverrideCommentPresent(String overrideComment);
    //abstract boolean isValidOverrideQuantity(Object stateQuantity, String overrideComment);
    abstract Map<String, Object> getOverriddenFields(Map<String, Object> row);

    public List<RequirementUploadLineItem> execute(List<Map<String, Object>> parsedJson, List<Requirement> requirements) {

        requirements = requirements;
        HashMap<Pair<String, String>, Map<String, Object>> uploadMap = new HashMap<>();

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
                requirementUploadLineItem.setFailureReason("");
                requirementUploadLineItem.setFsn(fsn);
                requirementUploadLineItem.setRowId(rowId);
                requirementUploadLineItems.add(requirementUploadLineItem);
            }

            Map<String, Object> overriddenValues = getOverriddenFields(row);
            Pair keyPair = new Pair<>(fsn, warehouse);
            uploadMap.put(keyPair, overriddenValues);

            requirements.forEach(
                    item -> item.setFsn("")
            );
            //get projection data

        }
        updateRecord(uploadMap);
        return requirementUploadLineItems;
    }

    private void updateRecord(Map<Pair<String, String>, Map<String, Object>> overriddenValues) {

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
