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

    protected static final int pageSize = 20;

    @Inject
    public RequirementRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    public List<Requirement> findRequirementByIds(List<Long> requirementIds) {
        List<Requirement> requirements = Lists.newArrayList();
        int pageNo = 0;
        PageRequest pageRequest = getPageRequest(pageNo, pageSize);
        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", requirementIds);
        Page<Requirement> page = findAllByNamedQuery("findRequirementByIds", params, pageRequest);
        requirements.addAll(page.getContent());
        if (page.isHasMore()) {
            pageNo += 1;
            pageRequest = getPageRequest(pageNo, pageSize);
            page = findAllByNamedQuery("findRequirementByIds", params, pageRequest);
            requirements.addAll(page.getContent());
        }
        return requirements;
    }

    public List<Requirement> findAllEnabledRequirements(String state) {
        List<Requirement> requirements = Lists.newArrayList();
        int pageNo = 0;
        PageRequest pageRequest = getPageRequest(pageNo, pageSize);
        Map<String, Object> params = Maps.newHashMap();
        params.put("state", state);
        Page<Requirement> page = findAllByNamedQuery("findEnabledRequirementsByState", params, pageRequest);
        requirements.addAll(page.getContent());
        if (page.isHasMore()) {
            pageNo += 1;
            pageRequest = getPageRequest(pageNo, pageSize);
            page = findAllByNamedQuery("findEnabledRequirementsByState", params, pageRequest);
            requirements.addAll(page.getContent());
        }
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
