package fk.retail.ip.projection.internal.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
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
@Table(name = "INTRANSIT")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(name = "Intransit.findAll", query = "SELECT i FROM Intransit i")
    , @NamedQuery(name = "Intransit.findByFsn", query = "SELECT i FROM Intransit i WHERE i.fsn = :fsn")
    , @NamedQuery(name = "Intransit.findByType", query = "SELECT i FROM Intransit i WHERE i.type = :type")
    , @NamedQuery(name = "Intransit.findByWarehouse", query = "SELECT i FROM Intransit i WHERE i.warehouse = :warehouse")
    , @NamedQuery(name = "Intransit.findByPendingPOs", query = "SELECT i FROM Intransit i WHERE i.pendingPOs = :pendingPOs")
    , @NamedQuery(name = "Intransit.findByOpenRequirements", query = "SELECT i FROM Intransit i WHERE i.openRequirements = :openRequirements")
})
@AttributeOverrides({
    @AttributeOverride(name = "createdAt", column = @Column(name = "stampCreated"))
    ,@AttributeOverride(name = "updatedAt", column = @Column(name = "stampCreated"))
})
public class Intransit extends AbstractEntity {

    @Size(max = 100)
    @Column(name = "fsn")
    private String fsn;
    @Size(max = 50)
    @Column(name = "type")
    private String type;
    @Size(max = 50)
    @Column(name = "warehouse")
    private String warehouse;
    @Column(name = "pendingPOs")
    private Integer pendingPOs;
    @Column(name = "openRequirements")
    private Integer openRequirements;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Intransit)) {
            return false;
        }
        Intransit other = (Intransit) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.flipkart.ip.db.entity.Intransit[ id=" + id + " ]";
    }
}
