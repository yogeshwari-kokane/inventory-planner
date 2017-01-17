package fk.retail.ip.projection.internal.entities;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Pragalathan M
 */
@Entity
@Table(name = "forecasts")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(name = "FsnForecast.findAll", query = "SELECT f FROM FsnForecast f")
    , @NamedQuery(name = "FsnForecast.findById", query = "SELECT f FROM FsnForecast f WHERE f.id = :id")
    , @NamedQuery(name = "FsnForecast.findByFsn", query = "SELECT f FROM FsnForecast f WHERE f.fsn = :fsn")
    , @NamedQuery(name = "FsnForecast.findByState", query = "SELECT f FROM FsnForecast f WHERE f.state = :state")
    , @NamedQuery(name = "FsnForecast.findByConfirmedT1", query = "SELECT f FROM FsnForecast f WHERE f.confirmedT1 = :confirmedT1")
    , @NamedQuery(name = "FsnForecast.findByConfirmedT2", query = "SELECT f FROM FsnForecast f WHERE f.confirmedT2 = :confirmedT2")
    , @NamedQuery(name = "FsnForecast.findByConfirmedT3", query = "SELECT f FROM FsnForecast f WHERE f.confirmedT3 = :confirmedT3")
    , @NamedQuery(name = "FsnForecast.findByConfirmedT4", query = "SELECT f FROM FsnForecast f WHERE f.confirmedT4 = :confirmedT4")
    , @NamedQuery(name = "FsnForecast.findBySystemProposedT1", query = "SELECT f FROM FsnForecast f WHERE f.systemProposedT1 = :systemProposedT1")
    , @NamedQuery(name = "FsnForecast.findBySystemProposedT2", query = "SELECT f FROM FsnForecast f WHERE f.systemProposedT2 = :systemProposedT2")
    , @NamedQuery(name = "FsnForecast.findBySystemProposedT3", query = "SELECT f FROM FsnForecast f WHERE f.systemProposedT3 = :systemProposedT3")
    , @NamedQuery(name = "FsnForecast.findBySystemProposedT4", query = "SELECT f FROM FsnForecast f WHERE f.systemProposedT4 = :systemProposedT4")
    , @NamedQuery(name = "FsnForecast.findByForecastDate", query = "SELECT f FROM FsnForecast f WHERE f.forecastDate = :forecastDate")
    , @NamedQuery(name = "FsnForecast.findByForecastPeriod", query = "SELECT f FROM FsnForecast f WHERE f.forecastPeriod = :forecastPeriod")
})
public class FsnForecast extends AbstractEntity {

    @Column(name = "fsn", insertable = false, updatable = false)
    private String fsn;
    @Column(name = "state", insertable = false, updatable = false)
    private String state;
    @Column(name = "confirmed_t1", insertable = false, updatable = false)
    private Float confirmedT1;
    @Column(name = "confirmed_t2", insertable = false, updatable = false)
    private Float confirmedT2;
    @Column(name = "confirmed_t3", insertable = false, updatable = false)
    private Float confirmedT3;
    @Column(name = "confirmed_t4", insertable = false, updatable = false)
    private Float confirmedT4;

    @Column(name = "system_proposed_t1", insertable = false, updatable = false)
    private Float systemProposedT1;
    @Column(name = "system_proposed_t2", insertable = false, updatable = false)
    private Float systemProposedT2;
    @Column(name = "system_proposed_t3", insertable = false, updatable = false)
    private Float systemProposedT3;
    @Column(name = "system_proposed_t4", insertable = false, updatable = false)
    private Float systemProposedT4;
    @Column(name = "model_name", insertable = false, updatable = false)
    private String modelName;
    @Column(name = "forecast_date", insertable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    private Date forecastDate;
    @Column(name = "forecast_period", insertable = false, updatable = false)
    private Integer forecastPeriod;
    @Column(name = "batch_id", insertable = false, updatable = false)
    private Integer batchId;
    @Column(name = "batch_item_count", insertable = false, updatable = false)
    private Integer batchItemCount;
    @Column(name = "pan_india_forecast", insertable = false, updatable = false)
    private String panIndiaForecast;
    @OneToMany(mappedBy = "fsnForecast", fetch = FetchType.LAZY)
    private List<WarehouseForecast> warehouseForecasts;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FsnForecast)) {
            return false;
        }
        FsnForecast other = (FsnForecast) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.flipkart.ip.db.entity.FsnForecast[ id=" + id + " ]";
    }
}
