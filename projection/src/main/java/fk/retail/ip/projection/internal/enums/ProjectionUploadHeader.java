package fk.retail.ip.projection.internal.enums;

import lombok.Getter;

/**
 * Created by nidhigupta.m on 08/01/17.
 */

@Getter
public enum ProjectionUploadHeader {
    projection_id("id1"),
    projection_item_id("id2"),
    fsn("fsn"),
    warehouse("warehouse"),
    supplier("supplier"),
    bd_supplier("bd_supplier"),
    bd_supplier_override_reason("bd_supplier_override_reason"),
    app("app"),
    bd_app("bd_app"),
    bd_app_override_reason("bd_app_override_reason"),
    sla("sla"),
    new_sla("new_sla"),
    quantity("quantity"),
    bd_quantity("bd_quantity"),
    bd_quantity_override_reason("bd_quantity_override_reason"),
    ipc_qty("ipc_qty"),
    ipc_qty_override_reason("ipc_qty_override_reason");

    private String displayName;

    ProjectionUploadHeader(String displayName) {
        this.displayName = displayName;
    }
}
