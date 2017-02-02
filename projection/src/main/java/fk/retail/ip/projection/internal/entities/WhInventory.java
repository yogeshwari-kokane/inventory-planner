package fk.retail.ip.projection.internal.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Pragalathan M<pragalathan.m@flipkart.com>
 */
@Entity
@Table(name = "wh_inventory")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(name = "WhInventory.findAll", query = "SELECT w FROM WhInventory w")
    , @NamedQuery(name = "WhInventory.findById", query = "SELECT w FROM WhInventory w WHERE w.id = :id")
    , @NamedQuery(name = "WhInventory.findByFsn", query = "SELECT w FROM WhInventory w WHERE w.fsn = :fsn")
    , @NamedQuery(name = "WhInventory.findByWarehouse", query = "SELECT w FROM WhInventory w WHERE w.warehouse = :warehouse")
    , @NamedQuery(name = "WhInventory.findByQuantity", query = "SELECT w FROM WhInventory w WHERE w.quantity = :quantity")
})
public class WhInventory extends AbstractEntity {

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "fsn")
    private String fsn;
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "warehouse")
    private String warehouse;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "b2b")
    private Integer b2b;
    @Column(name = "b2c")
    private Integer b2c;
    @Column(name = "fbf")
    private Integer fbf;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WhInventory)) {
            return false;
        }
        WhInventory other = (WhInventory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.flipkart.ip.db.entity.WhInventory[ id=" + id + " ]";
    }
}
