package fk.retail.ip.projection.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import fk.retail.ip.projection.internal.entities.Projection;
import fk.retail.ip.projection.internal.entities.ProjectionItem;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Pragalathan M
 */
@XmlRootElement
@Getter
@Setter
public class ProjectionLineItem {

    private final String fsn;
    private final String sku;
    private final Integer inventory;
    private final Integer intransit;
    private final String procurementType;

    private final Integer quantity;
    private final String warehouse;
    private final Integer mrp;
    private final Integer app;
    private final String supplier;
    private final boolean international;
    private final Integer sla;

    private int salesBand;
    private int pvBand;
    private String forecast;
    @JsonProperty("salesBucket-0")
    private Integer week0Sale;
    @JsonProperty("salesBucket-1")
    private Integer week1Sale;
    @JsonProperty("salesBucket-2")
    private Integer week2Sale;
    @JsonProperty("salesBucket-3")
    private Integer week3Sale;
    @JsonProperty("salesBucket-4")
    private Integer week4Sale;
    @JsonProperty("salesBucket-5")
    private Integer week5Sale;
    @JsonProperty("salesBucket-6")
    private Integer week6Sale;
    @JsonProperty("salesBucket-7")
    private Integer week7Sale;

    public ProjectionLineItem(ProjectionItem pi) {
        Projection p = pi.getProjection();
        fsn = p.getFsn();
        inventory = p.getInventory();
        intransit = p.getIntransit();
        sku = p.getSku();
        procurementType = p.getProcType();

        quantity = pi.getQty();
        warehouse = pi.getWarehouse();
        mrp = pi.getMrp();
        app = pi.getApp();
        supplier = pi.getSupplier();
        international = pi.isInternational();
        sla = pi.getSla();

//vertical=
//category
//super_category
//title
//brand
//sellingPrice_amount
//product_status
//qoh
//last_supplier
//last_app
//forecast = p.getForecastId()
//total_value
//mov_moq
//mov_moq_status
    }

}
