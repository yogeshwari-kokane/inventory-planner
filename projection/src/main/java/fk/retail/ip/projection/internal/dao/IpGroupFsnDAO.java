package fk.retail.ip.projection.internal.dao;

import fk.retail.ip.projection.internal.entities.IpGroupFsn;
import io.dropwizard.hibernate.AbstractDAO;
import java.util.Collection;
import java.util.List;
import org.hibernate.SessionFactory;

/**
 *
 * @author Pragalathan M
 */
public class IpGroupFsnDAO extends AbstractDAO<IpGroupFsn> {

    public IpGroupFsnDAO(SessionFactory factory) {
        super(factory);
    }

    public List<IpGroupFsn> find(Collection<String> fsns) {
        return list(namedQuery("IpGroupFsn.findGroupId").setParameterList("fsns", fsns));
    }
}
