package fk.retail.ip.projection.internal.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
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
@Table(name = "ip_group_fsns")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(name = "IpGroupFsn.findAll", query = "SELECT i FROM IpGroupFsn i")
    , @NamedQuery(name = "IpGroupFsn.findById", query = "SELECT i FROM IpGroupFsn i WHERE i.id = :id")
    , @NamedQuery(name = "IpGroupFsn.findByGroupId", query = "SELECT i FROM IpGroupFsn i WHERE i.groupId = :groupId")
    , @NamedQuery(name = "IpGroupFsn.findByFsn", query = "SELECT i FROM IpGroupFsn i WHERE i.fsn = :fsn"),})
@NamedNativeQuery(name = "IpGroupFsn.findGroupId", query = "SELECT f.fsn, SUBSTRING_INDEX(GROUP_CONCAT(f.group_id ORDER BY g.priority DESC), ',', 1) groupId "
        + " FROM ip_group_fsns f JOIN ip_groups g ON f.group_id = g.id"
        + " WHERE f.fsn IN (:fsns) AND g.tag = 'rp_planning' GROUP BY f.fsn", resultClass = IpGroupFsn.class)
public class IpGroupFsn extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Column(name = "group_id", insertable = false, updatable = false)
    private int groupId;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private IpGroup group;

    @Size(max = 50)
    @Column(name = "fsn")
    private String fsn;

    public IpGroupFsn(String fsn, Object groupId) {
        System.err.println("groupId = " + groupId.getClass());
//        this.groupId = groupId;
        this.fsn = fsn;
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
        if (!(object instanceof IpGroupFsn)) {
            return false;
        }
        IpGroupFsn other = (IpGroupFsn) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.flipkart.ip.db.dao.IpGroupFsn[ fsn=" + fsn + ", groupId" + groupId + " ]";
    }
}
