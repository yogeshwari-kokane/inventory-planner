package fk.retail.ip.projection.internal.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Pragalathan M
 */
@Entity
@Table(name = "fsn_week_sales_bucket_data")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(name = "WeeklySale.findByFsn", query = "SELECT f FROM WeeklySale f WHERE f.fsn IN (:fsns)")
})
public class WeeklySale extends AbstractEntity {

    @Column(insertable = false, updatable = false)
    private String fsn;

    @Column(insertable = false, updatable = false)
    private String warehouse;

    @Column(insertable = false, updatable = false)
    private int week;

    @Column(name = "sale_unit", insertable = false, updatable = false)
    private int saleUnit;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WeeklySale)) {
            return false;
        }
        WeeklySale other = (WeeklySale) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.flipkart.ip.db.dao.WeeklySale[ id=" + id + " ]";
    }

}
