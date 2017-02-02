package fk.retail.ip.requirement.model;

import java.util.List;
import lombok.Getter;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@Getter
public class DownloadRequirementRequest {

    List<Long> requirementIds;
    boolean lastAppSupplierRequired;
    String state;
}
