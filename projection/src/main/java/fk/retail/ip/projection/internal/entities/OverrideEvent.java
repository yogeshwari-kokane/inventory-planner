package fk.retail.ip.projection.internal.entities;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by nidhigupta.m on 05/01/17.
 */


@Entity
@Table(name = "region_override_events")
@XmlRootElement
public class OverrideEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    protected Long id;

    String fsn;

    String warehouse;

    @Column(name = "field_name")
    String fieldName;

    @Column(name = "override_type")
    String overrideType;

    @Column(name = "old_value")
    String oldValue;

    @Column(name = "new_value")
    String newValue;

    @Column(name = "created_by")
    String createdBy;

    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdAt;

    @Column(name = "override_entity")
    String overrideEntity;

    @Column(name = "parent_id")
    Long entityId;

    String state;

    String reason;

    public OverrideEvent(Long entityId, String fsn, String warehouse, String fieldName, String oldValue,
                         String newValue, String reason, String overrideType, String overrideEntity){

        this.entityId = entityId;
        this.fsn = fsn;
        this.warehouse = warehouse;
        this.fieldName = fieldName;
        this.reason = reason;
        this.overrideEntity = overrideEntity;
        this.overrideType = overrideType;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}
