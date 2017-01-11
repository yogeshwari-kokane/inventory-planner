package fk.retail.ip.projection.internal.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
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
 * @author Pragalathan M
 */
@Entity
@Table(name = "ip_groups")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(name = "IpGroup.findAll", query = "SELECT i FROM IpGroup i")
    , @NamedQuery(name = "IpGroup.findById", query = "SELECT i FROM IpGroup i WHERE i.id = :id")
    , @NamedQuery(name = "IpGroup.findByLastResolved", query = "SELECT i FROM IpGroup i WHERE i.lastResolved = :lastResolved")
    , @NamedQuery(name = "IpGroup.findByGroupName", query = "SELECT i FROM IpGroup i WHERE i.groupName = :groupName")
})
public class IpGroup extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Lob
    @Size(max = 65535)
    @Column(name = "group_def")
    private String groupDef;
    @Size(max = 50)
    @Column(name = "tag")
    private String tag;
    @Column(name = "last_resolved")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastResolved;
    @Size(max = 256)
    @Column(name = "group_name")
    private String groupName;
    @Column(name = "priority")
    private Integer priority;
    @Size(max = 20)
    @Column(name = "procurement_type")
    private String procurementType;
    @NotNull
    @Column(name = "group_version")
    private long groupVersion;
    @NotNull
    @Column(name = "mov_moq_enabled")
    private int movMoqEnabled;
    @NotNull
    @Column(name = "auto_publish")
    private boolean autoPublish;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IpGroup)) {
            return false;
        }
        IpGroup other = (IpGroup) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.flipkart.ip.db.dao.IpGroup[ id=" + id + " ]";
    }

}
