package fk.retail.ip.fdp.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Data
@JsonSnakeCase
public class FdpEntityPayload<T> {
    Object entityId;
    Date updatedAt;
    int schemaVersion;
    T data;
}
