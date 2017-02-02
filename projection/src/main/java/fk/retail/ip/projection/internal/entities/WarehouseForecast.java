package fk.retail.ip.projection.internal.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "forecast_regional")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(name = "WarehouseForecast.findAll", query = "SELECT f FROM WarehouseForecast f")
    , @NamedQuery(name = "WarehouseForecast.findByFsn", query = "SELECT f FROM WarehouseForecast f WHERE f.fsn IN (:fsns)")
    , @NamedQuery(name = "WarehouseForecast.findByWarehouse", query = "SELECT f FROM WarehouseForecast f WHERE f.warehouse = :warehouse")
    , @NamedQuery(name = "WarehouseForecast.findByForecast", query = "SELECT f FROM WarehouseForecast f WHERE f.forecast = :forecast")
})
public class WarehouseForecast extends AbstractEntity {

    @Column(name = "fsn", insertable = false, updatable = false)
    private String fsn;

    @Column(name = "warehouse", insertable = false, updatable = false)
    private String warehouse;

    @Column(name = "forecast", insertable = false, updatable = false)
    private String forecast;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forecast_id")
    private FsnForecast fsnForecast;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WarehouseForecast)) {
            return false;
        }
        WarehouseForecast other = (WarehouseForecast) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.flipkart.ip.db.entity.WarehouseForecast[ id=" + id + " ]";
    }

}
