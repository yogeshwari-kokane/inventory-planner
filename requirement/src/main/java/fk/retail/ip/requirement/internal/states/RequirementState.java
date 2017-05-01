package fk.retail.ip.requirement.internal.states;

import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.UploadOverrideFailureLineItem;
import fk.retail.ip.ssl.model.SupplierSelectionResponse;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.util.List;
import java.util.Map;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 21/02/17.
 */

public interface RequirementState {

    StreamingOutput download(List<Requirement> requirements, boolean isLastAppSupplierRequired);
    List<UploadOverrideFailureLineItem> upload(List<Requirement> requirements, List<RequirementDownloadLineItem> requirementDownloadLineItems, String userID,
                                               Map<String, String> fsnToVerticalMap, MultiKeyMap<String,SupplierSelectionResponse> fsnWhSupplierMap);
    Map<String, String> createFsnVerticalMap(List<Requirement> requirements);
    MultiKeyMap<String,SupplierSelectionResponse> createFsnWhSupplierMap(List<RequirementDownloadLineItem> requirementDownloadLineItems, List<Requirement> requirements);
}
