package fk.retail.ip.requirement.config;

import lombok.Data;

/**
 * Created by yogeshwari.k on 07/04/17.
 */
@Data
public class RequirementConfiguration {
    String viewPath;
    String url;
    String callbackUrl;
    String requirementQueueName;
}
