package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import fk.retail.ip.requirement.internal.entities.WeeklySale;
import fk.sp.common.extensions.jpa.Page;
import fk.sp.common.extensions.jpa.PageRequest;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;

/**
 * Created by nidhigupta.m on 27/01/17.
 */
public class WeeklySaleRepository extends SimpleJpaGenericRepository<WeeklySale, Long>{

    protected static final int pageSize = 20;

    public WeeklySaleRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }
    public List<WeeklySale> fetchWeeklySalesForFsns(Set<String> fsns) {
        List<WeeklySale> weeklySales = Lists.newArrayList();
        int pageNo = 0;
        PageRequest pageRequest = getPageRequest(pageNo, pageSize);
        Map<String, Object> params = Maps.newHashMap();
        params.put("fsns",fsns);
        Page<WeeklySale> page =  findAllByNamedQuery("fetchWeeklySalesForFsns", params, pageRequest);
        weeklySales.addAll(page.getContent());
        if (page.isHasMore()) {
            pageNo += 1;
            pageRequest = getPageRequest(pageNo, pageSize);
            page =  findAllByNamedQuery("fetchWeeklySalesForFsns", params, pageRequest);
            weeklySales.addAll(page.getContent());
        }
        return weeklySales;
    }
    private PageRequest getPageRequest(int pageNo, int pageSize){
        PageRequest pageRequest = PageRequest
                .builder()
                .pageNumber(pageNo)
                .pageSize(pageSize)
                .build();

        return pageRequest;
    }

}
