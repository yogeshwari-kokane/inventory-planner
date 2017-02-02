package fk.retail.ip.projection.internal.dao;

import fk.retail.ip.projection.internal.entities.WarehouseForecast;
import io.dropwizard.hibernate.AbstractDAO;
import java.util.Collection;
import java.util.List;
import org.hibernate.SessionFactory;

/**
 *
 * @author Pragalathan M
 */
public class WarehouseForecastDAO extends AbstractDAO<WarehouseForecast> {

    public WarehouseForecastDAO(SessionFactory factory) {
        super(factory);
    }

    public WarehouseForecast findById(Long id) {
        return get(id);
    }

    public long create(WarehouseForecast entity) {
        return persist(entity).getId();
    }

    public List<WarehouseForecast> find(Collection<String> fsns) {
        return list(namedQuery("WarehouseForecast.findByFsn").setParameterList("fsns", fsns));
    }
}
