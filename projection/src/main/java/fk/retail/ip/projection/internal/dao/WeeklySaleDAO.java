package fk.retail.ip.projection.internal.dao;

import org.hibernate.SessionFactory;

import java.util.Collection;
import java.util.List;

import fk.retail.ip.projection.internal.entities.WeeklySale;
import io.dropwizard.hibernate.AbstractDAO;

/**
 *
 * @author Pragalathan M
 */
public class WeeklySaleDAO extends AbstractDAO<WeeklySale> {

    public WeeklySaleDAO(SessionFactory factory) {
        super(factory);
    }

    public List<WeeklySale> find(Collection<String> fsns) {
        return list(namedQuery("WeeklySale.findByFsn").setParameterList("fsns", fsns));
    }
}

