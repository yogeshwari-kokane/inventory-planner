package fk.retail.ip.projection.internal.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Pragalathan M
 */
@Entity
@Table(name = "projection_states")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(name = "ProjectionItem.findAll", query = "SELECT p FROM ProjectionItem p")
    , @NamedQuery(name = "ProjectionItem.findById", query = "SELECT p FROM ProjectionItem p WHERE p.id = :id")
    , @NamedQuery(name = "ProjectionItem.findByState", query = "SELECT p FROM ProjectionItem p WHERE p.state = :state")
    , @NamedQuery(name = "ProjectionItem.findByPanIndia", query = "SELECT p FROM ProjectionItem p WHERE p.panIndia = :panIndia")
    , @NamedQuery(name = "ProjectionItem.findByCreatedBy", query = "SELECT p FROM ProjectionItem p WHERE p.createdBy = :createdBy")
    , @NamedQuery(name = "ProjectionItem.findByWarehouse", query = "SELECT p FROM ProjectionItem p WHERE p.warehouse = :warehouse")
    , @NamedQuery(name = "ProjectionItem.findBySupplier", query = "SELECT p FROM ProjectionItem p WHERE p.supplier = :supplier")
    , @NamedQuery(name = "ProjectionItem.findByInternational", query = "SELECT p FROM ProjectionItem p WHERE p.international = :international")
    , @NamedQuery(name = "ProjectionItem.findBySla", query = "SELECT p FROM ProjectionItem p WHERE p.sla = :sla")
    , @NamedQuery(name = "ProjectionItem.findByEnabled", query = "SELECT p FROM ProjectionItem p WHERE p.projection.enabled = :enabled")
    , @NamedQuery(name = "ProjectionItem.findByPrevStateId", query = "SELECT p FROM ProjectionItem p WHERE p.prevStateId = :prevStateId")
})
public class ProjectionItem extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "state")
    private String state;

    @Column(name = "pan_india")
    private Integer panIndia;

    @Column(name = "qty")
    private Integer qty;

    @Size(max = 255)
    @Column(name = "created_by")
    private String createdBy;

    @Size(max = 50)
    @Column(name = "warehouse")
    private String warehouse;

    @Column(name = "mrp")
    private Integer mrp;

    @Column(name = "app")
    private Integer app;

    @Size(max = 100)
    @Column(name = "supplier")
    private String supplier;

    @Size(max = 100)
    @Column(name = "comment")
    private String comment;

    @Column(name = "international")
    private boolean international;

    @Column(name = "sla")
    private Integer sla;

    @Size(max = 10)
    @Column(name = "mrp_currency")
    private String mrpCurrency;

    @Size(max = 10)
    @Column(name = "app_currency")
    private String appCurrency;

    @Size(max = 50)
    @Column(name = "ssl_id")
    private String sslId;

    @NotNull
    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "prev_state_id")
    private Long prevStateId;

    @ManyToOne
    @JoinColumn(name = "projection_id")
    private Projection projection;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectionItem)) {
            return false;
        }
        ProjectionItem other = (ProjectionItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.flipkart.ip.db.ProjectionItem[ id=" + id + " ]";
    }
}
