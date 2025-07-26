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
    private List<Operation> operationList;

    @PlanningScore
    private HardSoftScore score;

    public ProductionSchedule() {}

    public ProductionSchedule(List<Machine> machineList, List<Integer> startTimeRange, List<Operation> operationList) {
        this.machineList = machineList;
        this.startTimeRange = startTimeRange;
        this.operationList = operationList;
    }

    public List<Machine> getMachineList() {
        return machineList;
    }

    public List<Operation> getOperationList() {
        return operationList;
    }

    public List<Integer> getStartTimeRange() {
        return startTimeRange;
    }
}
