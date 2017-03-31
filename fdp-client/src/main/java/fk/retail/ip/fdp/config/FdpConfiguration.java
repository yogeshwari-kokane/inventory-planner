package fk.retail.ip.fdp.config;

import lombok.Data;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Data
public class FdpConfiguration {
    String schemaVersion;
    String url;
    String queueName;
    String org;
    String company;
    String namespace;
}
