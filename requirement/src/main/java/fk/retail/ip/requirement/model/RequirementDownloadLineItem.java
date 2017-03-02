package fk.retail.ip.requirement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fk.retail.ip.requirement.internal.entities.Requirement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@XmlRootElement
@Getter
@Setter
public class RequirementDownloadLineItem {

    private String fsn;
    private String warehouse;
    private int pvBand;
    private int salesBand;
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
    private int inventory;
    private int qoh;
    private String forcast;
    private int pendingPOQty;
    private int openReqQty;
    private int iwitIntransitQty;
    private int quantity;
    private String supplier;
    private Integer mrp;
    private Integer app;
    private String currency;
    private Integer sla;
    private boolean international;
    private String procType;
    private String overrideComment;

    public RequirementDownloadLineItem(Requirement req) {
        this.fsn = req.getFsn();
        this.warehouse = req.getWarehouse();
        this.inventory = req.getRequirementSnapshot().getInventoryQty();
        this.qoh = req.getRequirementSnapshot().getQoh();
        this.pendingPOQty = req.getRequirementSnapshot().getPendingPoQty();
        this.iwitIntransitQty = req.getRequirementSnapshot().getIwitIntransitQty();
        this.openReqQty = req.getRequirementSnapshot().getOpenReqQty();
        this.forcast = req.getRequirementSnapshot().getForecast();
        this.quantity = req.getQuantity();
        this.supplier = req.getSupplier();
        this.mrp = req.getMrp();
        this.app = req.getApp();
        this.currency = req.getAppCurrency();
        this.sla = req.getSla();
        this.international = req.isInternational();
        this.procType = req.getProcType();
        this.overrideComment = req.getOverrideComment();

    }

}
