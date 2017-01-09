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
    warehouse("warehouse");

    private String displayName;

    ProjectionUploadHeader(String displayName) {
        this.displayName = displayName;
    }
}
