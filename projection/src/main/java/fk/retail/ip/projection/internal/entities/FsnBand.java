package fk.retail.ip.projection.internal.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "fsn_bands")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(name = "FsnBand.findByFsn", query = "SELECT f FROM FsnBand f WHERE timeFrame='Last 30 Days' AND f.fsn IN (:fsns)")
})
public class FsnBand extends AbstractEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "fsn")
    private String fsn;

    @Basic(optional = false)
    @NotNull
    @Column(name = "sales_band")
    private int salesBand;

    @Basic(optional = false)
    @NotNull
    @Column(name = "pv_band")
    private int pvBand;

    @Size(max = 50)
    @Column(name = "time_frame")
    private String timeFrame;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FsnBand)) {
            return false;
        }
        FsnBand other = (FsnBand) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.flipkart.ip.db.entity.FsnBand[ id=" + id + " ]";
    }
}
