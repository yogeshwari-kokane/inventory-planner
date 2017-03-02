package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@Entity
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
@Table(name = "projection_states")
public class Requirement extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @NotNull
    private String fsn;

    @NotNull
    private String warehouse;

    @Column(name = "qty")
    private int quantity;

    private String supplier;

    private Integer mrp;

    private Integer app;

    private String mrpCurrency;

    private String appCurrency;

    private Integer sla;

    private boolean international;

    @NotNull
    private String state;

    private String procType;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "is_current")
    private boolean current;

    @Column(name = "comment")
    @Size(max = 100)
    private String overrideComment;

    private String createdBy;

    private Long sslId;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private RequirementSnapshot requirementSnapshot;

    //TODO: legacy code
    @Column(name = "prev_state_id")
    private Long previousStateId;

    //TODO: legacy code
    @Column(name = "pan_india")
    private Integer panIndiaQuantity;

    //TODO: legacy code
    @Column(name = "projection_id")
    private Long projectionId;

    public Requirement(Long id) {
        this.id = id;
    }

    public Requirement(Requirement other) {
        fsn = other.fsn;
        warehouse = other.warehouse;
        quantity = other.quantity;
        supplier = other.supplier;
        mrp = other.mrp;
        mrpCurrency = other.mrpCurrency;
        app = other.app;
        appCurrency = other.appCurrency;
        sla = other.sla;
        international = other.international;
        procType = other.procType;
        enabled = other.enabled;
        current = other.current;
        requirementSnapshot = other.requirementSnapshot;

        //TODO: legacy code
        projectionId = other.projectionId;
        panIndiaQuantity = other.panIndiaQuantity;
        sslId = other.sslId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Requirement)) {
            return false;
        }
        Requirement other = (Requirement) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.flipkart.ip.db.Requirement[ id=" + id + " ]";
    }
}
