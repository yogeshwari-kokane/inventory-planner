package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.sp.common.extensions.jpa.Page;
import fk.sp.common.extensions.jpa.PageRequest;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import java.util.Map;
import javax.inject.Provider;
import javax.persistence.EntityManager;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class RequirementRepository extends SimpleJpaGenericRepository<Requirement, Long> {

    protected static final int pageSize = 100;

    @Inject
    public RequirementRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    public List<Requirement> find(List<String> fsns, String state) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("fsns", fsns);
        params.put("state", state);
        return fetchRequirements("findRequirementByFsns", params);
    }

    public List<Requirement> find(List<Long> ids) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", ids);
        return fetchRequirements("findRequirementByIds", params);
    }

    /**
     * Fetches all the <b>enabled</b> requirements in the given {@code state}.
     *
     * @param state the state in which the requirement has to be
     * @return list of all the <b>enabled</b> requirements in the given
     * {@code state}.
     */
    public List<Requirement> find(String state) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("state", state);
        return fetchRequirements("findEnabledRequirementsByState", params);
    }


    private List<Requirement> fetchRequirements(String query, Map<String, Object> params) {
        List<Requirement> requirements = Lists.newArrayList();
        int pageNo = 0;
        Page<Requirement> page;
        do {
            PageRequest pageRequest = getPageRequest(pageNo, pageSize);
            page = findAllByNamedQuery(query, params, pageRequest);
            requirements.addAll(page.getContent());
            pageNo += 1;
        } while (page.isHasMore());

        return requirements;
    }

    private PageRequest getPageRequest(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest
                .builder()
                .pageNumber(pageNo)
                .pageSize(pageSize)
                .build();

        return pageRequest;
    }

}
