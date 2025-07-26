package de.markusglagla.timefold;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class Operation {

    @PlanningId
    private final String id;

    private final String requiredMachineType;

    private final int duration;

    private final String jobId;

    private final int indexInJob;

    @PlanningVariable(valueRangeProviderRefs = "machineRange")
    private Machine machine;

    @PlanningVariable(valueRangeProviderRefs = "startTimeRange")
    private Integer startTime;

    public Operation() {
        this.id = null;
        this.requiredMachineType = null;
        this.duration = 0;
        this.jobId = null;
        this.indexInJob = 0;
    }

    public Operation(String id, String requiredMachineType, int duration, String jobId, int indexInJob) {
        this.id = id;
        this.requiredMachineType = requiredMachineType;
        this.duration = duration;
        this.jobId = jobId;
        this.indexInJob = indexInJob;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "id='" + id + '\'' +
                ", requiredMachineType='" + requiredMachineType + '\'' +
                ", duration=" + duration +
                ", jobId='" + jobId + '\'' +
                ", indexInJob=" + indexInJob +
                ", machine=" + machine +
                ", startTime=" + startTime +
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

    public String getJobId() {
        return jobId;
    }

    public int getIndexInJob() {
        return indexInJob;
    }

    public Machine getMachine() {
        return machine;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public Integer getEndTime() {
        return startTime != null ? startTime + duration : null;
    }
}