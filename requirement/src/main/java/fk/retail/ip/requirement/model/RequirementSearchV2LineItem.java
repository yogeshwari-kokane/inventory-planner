package fk.retail.ip.requirement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import fk.retail.ip.requirement.internal.entities.Requirement;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * Created by yogeshwari.k on 09/05/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
@Data
@Getter
public class RequirementSearchV2LineItem {

    private String fsn;
    private String warehouse;
    private String warehouseName;
    private List<Integer> weeklySales;
    private Integer inventory;
    private Integer qoh;
    private String forecast;
    private double totalValue;
    private Integer intransitQty;
    private Integer qty;
    private String supplier;
    private Integer mrp;
    private Double app;
    private String appCurrency;
    private Integer sla;
    private String requirementId;
    private String createdBy;
    private Integer international;
    private String mrpCurrency;
    private Integer poId;
    private String overrideReason;

    public RequirementSearchV2LineItem(Requirement req) {

        this.requirementId = req.getId();
        this.fsn = req.getFsn();

        this.warehouse = req.getWarehouse();
        if (req.getRequirementSnapshot() != null) {
            this.inventory = req.getRequirementSnapshot().getInventoryQty();
            this.qoh = req.getRequirementSnapshot().getQoh();
            Integer iwitQuantity = req.getRequirementSnapshot().getIwitIntransitQty();
            Integer pendingPOQty = req.getRequirementSnapshot().getPendingPoQty();
            Integer openReqQty = req.getRequirementSnapshot().getOpenReqQty();
            this.intransitQty = (iwitQuantity != null ? iwitQuantity : 0);
            this.intransitQty += (pendingPOQty != null ? pendingPOQty : 0);
            this.intransitQty += (openReqQty != null ? openReqQty : 0);
            this.forecast = req.getRequirementSnapshot().getForecast();
        }
        this.qty = (int) req.getQuantity();
        this.supplier = req.getSupplier();
        this.mrp = req.getMrp();
        this.app = req.getApp() != null ? req.getApp() : 0;
        this.appCurrency = req.getCurrency();
        this.totalValue = this.app * this.qty;
        this.sla = req.getSla();
        this.createdBy = req.getCreatedBy();
        this.international = req.isInternational() ? 1 : 0;
        this.mrpCurrency = req.getMrpCurrency();
        this.poId = req.getPoId();
        this.overrideReason = req.getOverrideComment();
    }
}
