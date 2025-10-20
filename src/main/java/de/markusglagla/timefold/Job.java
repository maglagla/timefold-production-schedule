package de.markusglagla.timefold;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class Job {

    @PlanningId
    private final String id;

    private final String requiredMachineType;

    private final int duration;

    private final String orderId;

    private final int indexInOrder;

    @PlanningVariable(valueRangeProviderRefs = "machineRange")
    private Machine assignedMachine;

    @PlanningVariable(valueRangeProviderRefs = "startTimeRange")
    private Integer assignedStartTime;

    public Job() {
        this.id = null;
        this.requiredMachineType = null;
        this.duration = 0;
        this.orderId = null;
        this.indexInOrder = 0;
    }

    public Job(String id, String requiredMachineType, int duration, String orderId, int indexInJob) {
        this.id = id;
        this.requiredMachineType = requiredMachineType;
        this.duration = duration;
        this.orderId = orderId;
        this.indexInOrder = indexInJob;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", requiredMachineType='" + requiredMachineType + '\'' +
                ", duration=" + duration +
                ", orderId='" + orderId + '\'' +
                ", indexInOrder=" + indexInOrder +
                ", assignedMachine=" + assignedMachine +
                ", assignedStartTime=" + assignedStartTime +
                '}';
    }

    public String getId() {
        return id;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getRequiredMachineType() {
        return requiredMachineType;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getIndexInOrder() {
        return indexInOrder;
    }

    public Machine getAssignedMachine() {
        return assignedMachine;
    }

    public Integer getAssignedStartTime() {
        return assignedStartTime;
    }

    public Integer getEndTime() {
        return assignedStartTime != null ? assignedStartTime + duration : null;
    }
}