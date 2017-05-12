package fk.retail.ip.requirement.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by agarwal.vaibhav on 10/05/17.
 */
@Data
public class StencilConfigModel {
    Map<String, Map<String, String>> stateStencilMapping;
}
