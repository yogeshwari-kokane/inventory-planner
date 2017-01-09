package fk.retail.ip.projection.internal.dao;

import org.hibernate.SessionFactory;

import java.util.List;

import fk.retail.ip.projection.internal.entities.ProjectionItem;
import io.dropwizard.hibernate.AbstractDAO;

/**
 *
 * @author Pragalathan M
 */
public class ProjectionItemDAO extends AbstractDAO<ProjectionItem> {

    public ProjectionItemDAO(SessionFactory factory) {
        super(factory);
    }

    public ProjectionItem findById(Long id) {
        return get(id);
    }

    public long create(ProjectionItem entity) {
        return persist(entity).getId();
    }

    public List<ProjectionItem> findAll() {
        return list(namedQuery("ProjectionItem.findByEnabled").setBoolean("enabled", true).setMaxResults(10));
    }
}
