package de.markusglagla.timefold;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;

public class ProductionConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                matchingMachineType(factory),
                noOverlapOnSameMachine(factory),
                processOperationsInSequence(factory),
                totalMakespan(factory)
        };
    }

    private Constraint matchingMachineType(ConstraintFactory factory) {
        return factory.forEach(Operation.class)
                .filter(op -> !op.getRequiredMachineType().equals(op.getMachine().getType()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Matching machine type");
    }

    private Constraint noOverlapOnSameMachine(ConstraintFactory factory) {
        return factory.forEachUniquePair(Operation.class,
                        Joiners.equal(Operation::getMachine))
                .filter((op1,op2) -> op1.getStartTime() != null && op2.getStartTime() != null &&
                        op1.getStartTime() < op2.getEndTime() &&
                        op2.getStartTime() < op1.getEndTime())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("No overlap on same machine");
    }

    private Constraint processOperationsInSequence(ConstraintFactory factory) {
        return factory.forEachUniquePair(Operation.class,
                        Joiners.equal(Operation::getJobId),
                        Joiners.lessThan(Operation::getIndexInJob))
                .filter((op1, op2) -> op1.getStartTime() != null && op2.getStartTime() != null &&
                        op1.getIndexInJob() < op2.getIndexInJob() &&
                        op2.getStartTime() < op1.getEndTime())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Process operations in sequence");
    }

    private Constraint totalMakespan(ConstraintFactory factory) {
        return factory.forEach(Operation.class)
                .filter(ja -> ja.getStartTime() != null)
                .penalize(HardSoftScore.ONE_SOFT, Operation::getEndTime)
                .asConstraint("Total makespan");
    }

}
