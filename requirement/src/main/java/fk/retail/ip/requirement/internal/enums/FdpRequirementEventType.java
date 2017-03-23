package fk.retail.ip.requirement.internal.enums;

/**
 * Created by yogeshwari.k on 23/03/17.
 */
public enum FdpRequirementEventType {

    SUPPLIER_ASSIGNED("Supplier Assigned"),
    APP_ASSIGNED("App Assigned"),
    PROJECTION_CREATED("Projection Created"),
    IPC_QUANTITY_OVERRIDE("IPC Quantity Override"),
    CDO_QUANTITY_OVERRIDE("CDO Quantity Override"),
    CDO_APP_OVERRIDE("CDO App Override"),
    CDO_SLA_OVERRIDE("CDO Sla Override"),
    CDO_SUPPLIER_OVERRIDE("CDO Supplier Override"),
    APPROVE("Approve"),
    CANCEL("Cancel");

    private String key;

    FdpRequirementEventType(String key) {
        this.key = key;
    }

    @Override
    public String toString() { return this.key;}

    }
