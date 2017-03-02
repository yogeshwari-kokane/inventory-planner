package fk.retail.ip.requirement.internal.command.upload;


import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.RequirementUploadLineItem;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public abstract class UploadCommand {

    abstract Map<String, Object> validateAndSetStateSpecific(RequirementDownloadLineItem row);

    private final RequirementRepository requirementRepository;

    public UploadCommand(RequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    public List<RequirementUploadLineItem> execute(List<RequirementDownloadLineItem> requirementDownloadLineItems, List<Requirement> requirements) {

        Map<Pair<String,String>,Requirement> requirementMap = requirements.stream().collect(Collectors.toMap(requirement -> {
            return new ImmutablePair<String, String>(requirement.getFsn(), requirement.getWarehouse());
        }, requirement -> requirement));

        ArrayList<RequirementUploadLineItem> requirementUploadLineItems = new ArrayList<>();
        Integer rowCount = 0;
        for(RequirementDownloadLineItem row : requirementDownloadLineItems) {
            RequirementUploadLineItem requirementUploadLineItem = new RequirementUploadLineItem();
            rowCount += 1;
            String fsn = row.getFsn();
            String warehouse = row.getWarehouseName();

            String genericComment = validateGenericRowColumns(fsn, warehouse);

            /*TODO : remove null checks*/
            if (genericComment != null) {
                requirementUploadLineItem.setFailureReason(genericComment);
                requirementUploadLineItem.setFsn(fsn);
                requirementUploadLineItem.setRowNumber(rowCount.toString());
                requirementUploadLineItem.setWarehouse(warehouse);
                requirementUploadLineItems.add(requirementUploadLineItem);
                continue;
            }

            Map<String, Object> overriddenValues = validateAndSetStateSpecific(row);

            if (overriddenValues.containsKey("failure")) {
                requirementUploadLineItem.setFailureReason(overriddenValues.get("failure").toString());
                requirementUploadLineItem.setFsn(fsn);
                requirementUploadLineItem.setRowNumber(rowCount.toString());
                requirementUploadLineItem.setWarehouse(warehouse);
                requirementUploadLineItems.add(requirementUploadLineItem);

            } else {

                Requirement requirement = requirementMap.get(new ImmutablePair<>(fsn, warehouse));
                /*TODO check if requirement is present or not*/

                if (overriddenValues.containsKey("quantity")) {
                    System.out.println("quantity to be : " + overriddenValues.get("quantity"));
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
                    System.out.println("comment to be : " + overriddenValues.get("overrideComment"));
                    requirement.setOverrideComment(overriddenValues.get("overrideComment").toString());
                }

                requirementRepository.persist(requirement);
            }

        }
        return requirementUploadLineItems;
    }

    private String validateGenericRowColumns(String fsn, String warehouse) {
        if (fsn == null || warehouse == null) {
            log.debug("fsn and/or warehouse is missing from requirement");
            return "fsn, and/or warehouse is missing";
        }else {
            return null;
        }
    }

}
