package fk.retail.ip.requirement.service;

import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.AbstractEntity;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.json.JSONObject;

/**
 *
 * @author Pragalathan M<pragalathan.m@flipkart.com>
 */
public class ApprovalService<E extends AbstractEntity> {

    private JSONObject actions;

    @Inject
    public ApprovalService(String actionConfiguration) {
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(actionConfiguration))) {
            actions = new JSONObject(CharStreams.toString(reader));
        } catch (Exception ex) {
            throw new RuntimeException("Error in reading config file", ex);
        }
    }

    public void changeState(List<E> items,
            String userId,
            String action,
            Function<E, String> getter,
            StageChangeAction<E>... actionListeners) {
        JSONObject stateMachine = this.actions.getJSONObject(action);
        String fromState = stateMachine.getString("currentState");
        String toState = stateMachine.getString("nextState");
        boolean forward = stateMachine.getBoolean("isForward");
        validate(items, fromState, getter);
        for (StageChangeAction<E> consumer : actionListeners) {
            items.forEach(e -> consumer.execute(userId, fromState, toState, forward, e));
        }
    }

    private void validate(List<E> items, String fromState, Function<E, String> getter) {
        for (E item : items) {
            String currentState = getter.apply(item);
            if (!currentState.equals(fromState)) {
                throw new IllegalStateException("Entity[id=" + item.getId() + "] is not in " + fromState + " state");
            }
        }
    }

    public static interface StageChangeAction<E extends AbstractEntity> {

        void execute(String userId,
                String fromState,
                String toState,
                boolean forward,
                E entity);
    }

    public static class CopyOnStateChangeAction implements StageChangeAction<Requirement> {

        private RequirementRepository repository;

        public CopyOnStateChangeAction(RequirementRepository requirementRepository) {
            this.repository = requirementRepository;
        }

        @Override
        public void execute(String userId, String fromState, String toState, boolean forward, Requirement entity) {
            if (forward) {
                List<Requirement> toStateEntities = repository.find(Arrays.asList(entity.getFsn()), toState);
                if (toStateEntities.isEmpty()) {
                    Requirement newEntity = new Requirement(entity);
                    newEntity.setState(toState);
//            newEntity.setUpdatedBy(userId);
                    repository.persist(newEntity);
                    entity.setCurrent(false);
                } else {
                    Requirement toStateEntity = toStateEntities.get(0);
                    toStateEntity.setQuantity(entity.getQuantity());
                    toStateEntity.setSupplier(entity.getSupplier());
                    toStateEntity.setApp(entity.getApp());
                    toStateEntity.setSla(entity.getSla());
                    toStateEntity.setPreviousStateId(entity.getId());
                    entity.setCurrent(false);
                }
            } else {
                List<Requirement> toStateEntities = repository.find(Arrays.asList(entity.getFsn()), toState);
                Requirement toStateEntity = toStateEntities.get(0);
                toStateEntity.setCurrent(true);
                entity.setCurrent(false);
            }
        }
    }
}
