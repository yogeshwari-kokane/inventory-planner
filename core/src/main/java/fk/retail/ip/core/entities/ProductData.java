package fk.retail.ip.core.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Created by nidhigupta.m on 16/05/17.
 */
@Entity
@Table(name = "product_data")
@Data
public class ProductData extends ReadOnlyEntity {

    @NotNull
    private String fsn;
    private String businessUnit;
    private String vertical;
    private String category;
    private String superCategory;
    private String title;
    private String publisher;
    private String brand;
    private int pvBand;
    private int salesBand;
    private int atp;
    private int qoh;
    private Date lastPoDate;
//    private double nlc;
//    private double fsp;


}

