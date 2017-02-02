package fk.retail.ip.projection.internal.dao;

import org.hibernate.SessionFactory;

import java.util.Collection;
import java.util.List;

import fk.retail.ip.projection.internal.entities.WarehouseForecast;
import io.dropwizard.hibernate.AbstractDAO;

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

