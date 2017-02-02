package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@Entity
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
@Table(name = "REQUIREMENT")
public class Requirement extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @NotNull
    private String fsn;

    @NotNull
    private String warehouse;

    @NotNull
    private int quantity;

    private String supplier;

    private Integer mrp;

    private Integer app;

    private String currency;

    private Integer sla;

    private boolean international;

    @NotNull
    private String state;

    private String procType;

    @Column(name = "is_enabled")
    private boolean enabled;

    @Column(name = "is_current")
    private boolean current;

    @Size(max = 100)
    private String overrideComment;

    private String createdBy;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private RequirementSnapshot requirementSnapshot;

    public Requirement(Long id) {
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
        if (!(object instanceof Requirement)) {
            return false;
        }
        Requirement other = (Requirement) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.flipkart.ip.db.Requirement[ id=" + id + " ]";
    }

}
