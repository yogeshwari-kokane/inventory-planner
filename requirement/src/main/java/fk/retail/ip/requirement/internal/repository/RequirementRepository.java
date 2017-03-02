package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.sp.common.extensions.jpa.Page;
import fk.sp.common.extensions.jpa.PageRequest;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@Slf4j
public class RequirementRepository extends SimpleJpaGenericRepository<Requirement, Long> {

    public static final int PAGE_SIZE = 1000;

    @Inject
    public RequirementRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    public List<Requirement> find(Collection<String> fsn, String state) {
        TypedQuery<Requirement> query = getEntityManager().createNamedQuery("findRequirementByFsnState", Requirement.class);
        query.setParameter("fsns", fsn);
        query.setParameter("state", state);
        return query.getResultList();
    }

    public List<Requirement> findRequirements(List<Long> projectionIds, String requirementState, Map<String, Object> filters) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Requirement> criteriaQuery = criteriaBuilder.createQuery(Requirement.class);
        Root<Requirement> requirementRoot = criteriaQuery.from(Requirement.class);
        CriteriaQuery<Requirement> select = criteriaQuery.select(requirementRoot);
        List<Predicate> predicates = Lists.newArrayList();
        Predicate predicate = criteriaBuilder.equal(requirementRoot.get("current"), 1);
        predicates.add(predicate);
        predicate = criteriaBuilder.equal(requirementRoot.get("state"), requirementState);
        predicates.add(predicate);
        if (projectionIds != null && !projectionIds.isEmpty()) {
            predicate = criteriaBuilder.isTrue(requirementRoot.get("projectionId").in(projectionIds));
            predicates.add(predicate);
        }
        if (filters.containsKey("fsns") && !filters.get("fsns").toString().isEmpty()) {
            predicate = criteriaBuilder.equal(requirementRoot.get("fsn"), filters.get("fsns"));
            predicates.add(predicate);
        }
        if (filters.containsKey("international") && !filters.get("international").toString().isEmpty()) {
            predicate = criteriaBuilder.equal(requirementRoot.get("international"), Integer.parseInt(filters.get("international").toString()));
            predicates.add(predicate);
        }
        if (filters.containsKey("price_from") && !filters.get("price_from").toString().isEmpty()) {
            predicate = criteriaBuilder.greaterThanOrEqualTo(requirementRoot.get("app"), Integer.parseInt(filters.get("price_from").toString()));
            predicates.add(predicate);
        }
        if (filters.containsKey("price_to") && !filters.get("price_to").toString().isEmpty()) {
            predicate = criteriaBuilder.lessThanOrEqualTo(requirementRoot.get("app"), Integer.parseInt(filters.get("price_to").toString()));
            predicates.add(predicate);
        }
//        if (filters.get("group")!=null) {
//            predicate = criteriaBuilder.greaterThanOrEqualTo(requirementRoot.get("requirementSnapshot").get("group").get("name"), filters.get("group"));
//            predicates.add(predicate);
//        }

        select.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        TypedQuery<Requirement> query = entityManager.createQuery(select);
        return query.getResultList();
    }

    public List<Requirement> find(List<Long> ids) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", ids);
        return fetchRequirements("findRequirementByIds", params);
    }

    //TODO: legacy code
    public List<Requirement> findByProjectionIds(List<Long> projectionIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("projectionIds", projectionIds);
        return fetchRequirements("findRequirementByProjectionIds", params);
    }

    //TODO: legacy code
    public int updateProjection(Collection<Long> projectionIds, String toState) {
        return getEntityManager()
                .createNativeQuery("UPDATE projections SET current_state=:state WHERE id IN (:ids)")
                .setParameter("ids", projectionIds)
                .setParameter("state", toState)
                .executeUpdate();
    }

    /**
     * Fetches all the <b>enabled</b> requirements in the given {@code state}.
     *
     * @param state the state in which the requirement has to be
     * @param pageNumber the page to fetch
     * @return list of all the <b>enabled</b> requirements in the given
     * {@code state} and for the given {@code pageNumber} page.
     */
    public List<Requirement> find(String state, Boolean current, int pageNumber) {
        log.info("Fetching {} records for page: {}", PAGE_SIZE, pageNumber);
        TypedQuery<Requirement> query = getEntityManager().createNamedQuery("findEnabledRequirementsByState", Requirement.class);
        query.setParameter("state", state);
        if (current != null) {

        }

        return query.setFirstResult((pageNumber - 1) * PAGE_SIZE)
                .setMaxResults(PAGE_SIZE)
                .getResultList();
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
            page = fetchRequirements(query, params, pageNo);
            requirements.addAll(page.getContent());
            pageNo += 1;
        } while (page.isHasMore());

        return requirements;
    }

    private Page<Requirement> fetchRequirements(String query, Map<String, Object> params, int pageNumber) {
        PageRequest pageRequest = getPageRequest(pageNumber, PAGE_SIZE);
        log.info("Fetching {} records for page: {}", PAGE_SIZE, pageNumber);
        return findAllByNamedQuery(query, params, pageRequest);
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
