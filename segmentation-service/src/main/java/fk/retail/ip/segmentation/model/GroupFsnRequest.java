package fk.retail.ip.segmentation.model;

import io.dropwizard.jackson.JsonSnakeCase;
import java.util.List;
import lombok.Data;

/**
 * Created by nidhigupta.m on 05/05/17.
 */

@Data
@JsonSnakeCase
public class GroupFsnRequest {
    String groupName;
    List<String> fsnList;
}
