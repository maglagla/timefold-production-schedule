package de.markusglagla.timefold;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@PlanningSolution
public class ProductionSchedule {

    @ValueRangeProvider(id = "machineRange")
    private List<Machine> machineList;

    @ValueRangeProvider(id = "startTimeRange")
    private List<Integer> startTimeRange;

    @PlanningEntityCollectionProperty
    private List<Job> jobList;

    @PlanningScore
    private HardSoftScore score;

    public ProductionSchedule() {}

    public ProductionSchedule(List<Machine> machineList, List<Integer> startTimeRange, List<Job> jobList) {
        this.machineList = machineList;
        this.startTimeRange = startTimeRange;
        this.jobList = jobList;
    }

    public List<Machine> getMachineList() {
        return machineList;
    }

    public List<Job> getJobList() {
        return jobList;
    }

    public List<Integer> getStartTimeRange() {
        return startTimeRange;
    }
}
