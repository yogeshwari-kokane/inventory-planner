package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Entity;

import fk.retail.ip.core.entities.ReadOnlyEntity;
import lombok.Data;

/**
 * Created by yogeshwari.k on 21/02/17.
 */

@Entity
@Data
public class Warehouse extends ReadOnlyEntity {

    private String code;
    private String name;
}
