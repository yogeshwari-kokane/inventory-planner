package fk.retail.ip.core.entities;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "ip_group_fsns")
public class GroupFsn extends ReadOnlyEntity {

    String fsn;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "group_id")
    IPGroup group;

    public GroupFsn(String fsn, IPGroup group, Date date) {
        this.fsn = fsn;
        this.group = group;
        this.createdAt = date;
    }
}
