package fk.retail.ip.requirement.internal.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by nidhigupta.m on 03/02/17.
 */

@Entity
@Table(name = "product_detail")
@XmlRootElement
@Immutable
@Data
@NoArgsConstructor
public class ProductInfo extends AbstractEntity {
    @NotNull
    private String fsn;

    private String vertical;
    private String category;
    private String superCategory;
    private String title;
    private String brand;
    private int fsp;

}
