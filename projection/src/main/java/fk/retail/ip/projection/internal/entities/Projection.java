package fk.retail.ip.projection.internal.entities;

import fk.retail.ip.projection.internal.command.OverrideProjectionCommand;
import fk.retail.ip.projection.internal.exception.ProjectionOverrideException;
import fk.retail.ip.projection.internal.factory.ProjectionOverrideFactory;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Pragalathan M
 */
@Entity
@Table(name = "projections")
@XmlRootElement
@Getter
@Setter
@NamedQueries({
    @NamedQuery(name = "Projection.findAll", query = "SELECT p FROM Projection p")
    , @NamedQuery(name = "Projection.findById", query = "SELECT p FROM Projection p WHERE p.id = :id")
    , @NamedQuery(name = "Projection.findByFsn", query = "SELECT p FROM Projection p WHERE p.fsn = :fsn")
    , @NamedQuery(name = "Projection.findByCurrentState", query = "SELECT p FROM Projection p WHERE p.currentState = :currentState")
    , @NamedQuery(name = "Projection.findByEnabled", query = "SELECT p FROM Projection p WHERE p.enabled = :enabled")
    , @NamedQuery(name = "Projection.findBySku", query = "SELECT p FROM Projection p WHERE p.sku = :sku")
    , @NamedQuery(name = "Projection.findByProcType", query = "SELECT p FROM Projection p WHERE p.procType = :procType")
    , @NamedQuery(name = "Projection.findByForecastId", query = "SELECT p FROM Projection p WHERE p.forecastId = :forecastId")
    , @NamedQuery(name = "Projection.findByPolicyId", query = "SELECT p FROM Projection p WHERE p.policyId = :policyId")
    , @NamedQuery(name = "Projection.findByGroupId", query = "SELECT p FROM Projection p WHERE p.groupId = :groupId")
})
public class Projection extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Column(name = "fsn")
    private String fsn;

    @Basic(optional = false)
    @NotNull
    @Column(name = "current_state")
    private String currentState;

    @Column(name = "dirty")
    private Boolean dirty;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "intransit")
    private Integer intransit;

    @Column(name = "inventory")
    private Integer inventory;

    @Column(name = "sku")
    private String sku;

    @Column(name = "proc_type")
    private String procType;

    @Column(name = "forecast_id")
    private BigInteger forecastId;

    @Column(name = "policy_id")
    private String policyId;

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "error")
    private String error;

    @OneToMany(mappedBy = "projection", fetch = FetchType.LAZY)
    private List<ProjectionItem> projectionStates = new ArrayList<>();

    public Projection() {
    }

    public Projection(Long id) {
        this.id = id;
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
        if (!(object instanceof Projection)) {
            return false;
        }
        Projection other = (Projection) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.flipkart.ip.db.Projection[ id=" + id + " ]";
    }

    public void overrideProjection(ProjectionOverrideFactory projectionOverrideFactory, Map<String, Object> overrideRow) throws ProjectionOverrideException {
        OverrideProjectionCommand overrideProjectionCommand = projectionOverrideFactory.getStateOverrideProjectionCommand(this.currentState);
        overrideProjectionCommand.withOverrideRow(overrideRow).execute();
    }
}
