package fk.retail.ip.requirement.internal.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@Data
@NoArgsConstructor
public class RequirementApprovalTransitionV2 extends ReadOnlyEntity {
    long groupId;
    String fromState;
    String toState;
    boolean forward;
}
