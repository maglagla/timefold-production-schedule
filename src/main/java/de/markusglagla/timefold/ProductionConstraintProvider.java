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
                processJobsInSequence(factory),
                totalMakespan(factory)
        };
    }

    private Constraint matchingMachineType(ConstraintFactory factory) {
        return factory.forEach(Job.class)
                .filter(job -> !job.getRequiredMachineType().equals(job.getAssignedMachine().getType()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Matching machine type");
    }

    private Constraint noOverlapOnSameMachine(ConstraintFactory factory) {
        return factory.forEachUniquePair(Job.class,
                        Joiners.equal(Job::getAssignedMachine))
                .filter((job1,job2) -> job1.getAssignedStartTime() != null && job2.getAssignedStartTime() != null &&
                        job1.getAssignedStartTime() < job2.getEndTime() &&
                        job2.getAssignedStartTime() < job1.getEndTime())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("No overlap on same machine");
    }

    private Constraint processJobsInSequence(ConstraintFactory factory) {
        return factory.forEachUniquePair(Job.class,
                        Joiners.equal(Job::getOrderId),
                        Joiners.lessThan(Job::getIndexInOrder))
                .filter((job1, job2) -> job1.getAssignedStartTime() != null && job2.getAssignedStartTime() != null &&
                        job1.getIndexInOrder() < job2.getIndexInOrder() &&
                        job2.getAssignedStartTime() < job1.getEndTime())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Process jobs in sequence");
    }

    private Constraint totalMakespan(ConstraintFactory factory) {
        return factory.forEach(Job.class)
                .filter(job -> job.getAssignedStartTime() != null)
                .penalize(HardSoftScore.ONE_SOFT, Job::getEndTime)
                .asConstraint("Total makespan");
    }

}
