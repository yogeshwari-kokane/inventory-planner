package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import fk.retail.ip.core.entities.ReadOnlyEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by nidhigupta.m on 27/04/17.
 */

@Entity
@Table(name = "fsn_sales_data")
@XmlRootElement
@Data
@NoArgsConstructor
public class FsnSales extends ReadOnlyEntity {
    String fsn;
    int salesTime;
    int salesQuantity;
}
