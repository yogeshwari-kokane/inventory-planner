package fk.retail.ip.requirement.internal.entities;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import fk.retail.ip.core.entities.AbstractEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by nidhigupta.m on 03/02/17.
 */

@Entity
//todo:change this table name
@Table(name = "product_detail")
@XmlRootElement
@Data
@NoArgsConstructor
public class ProductInfo extends AbstractEntity {
    @NotNull
    private String fsn;
    private String businessUnit;
    private String vertical;
    private String category;
    private String superCategory;
    private String title;
    private String brand;
    private int fsp;
    private int pvBand;
    private int salesBand;
    private int atp;
    private Date lastPoDate;
    private String publisher;
}
