package fk.retail.ip.projection.internal.dao;

import fk.retail.ip.projection.internal.entities.Projection;
import io.dropwizard.hibernate.AbstractDAO;
import java.util.List;
import org.hibernate.SessionFactory;

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
