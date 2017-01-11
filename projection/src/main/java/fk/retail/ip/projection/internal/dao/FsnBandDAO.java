package fk.retail.ip.projection.internal.dao;

import org.hibernate.SessionFactory;

import java.util.Collection;
import java.util.List;

import fk.retail.ip.projection.internal.entities.FsnBand;
import io.dropwizard.hibernate.AbstractDAO;

/**
 *
 * @author Pragalathan M
 */
public class FsnBandDAO extends AbstractDAO<FsnBand> {

    public FsnBandDAO(SessionFactory factory) {
        super(factory);
    }

    public List<FsnBand> find(Collection<String> fsns) {
        return list(namedQuery("FsnBand.findByFsn").setParameterList("fsns", fsns));
    }
}

