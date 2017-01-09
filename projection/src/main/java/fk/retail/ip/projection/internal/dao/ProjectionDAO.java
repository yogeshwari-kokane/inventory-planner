package fk.retail.ip.projection.internal.dao;


import org.hibernate.SessionFactory;

import java.util.List;

import fk.retail.ip.projection.internal.entities.Projection;
import io.dropwizard.hibernate.AbstractDAO;

/**
 *
 * @author Pragalathan M
 */
public class ProjectionDAO extends AbstractDAO<Projection> {

    public ProjectionDAO(SessionFactory factory) {
        super(factory);
    }

    public Projection findById(Long id) {
        return get(id);
    }

    public long create(Projection entity) {
        return persist(entity).getId();
    }

    public List<Projection> findAll() {
        return list(namedQuery("Projection.findByEnabled").setBoolean("enabled", true).setMaxResults(10));
    }
}
