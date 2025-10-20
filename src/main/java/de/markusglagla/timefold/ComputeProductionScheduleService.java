package de.markusglagla.timefold;

import ai.timefold.solver.core.api.solver.SolverManager;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ComputeProductionScheduleService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ComputeProductionScheduleService.class);

    private final SolverManager<ProductionSchedule, Long> solverManager;

    public ComputeProductionScheduleService(SolverManager<ProductionSchedule, Long> solverManager) {
        this.solverManager = solverManager;
    }

    public void compute() throws ExecutionException, InterruptedException {

        ProductionSchedule problem = loadProblem();
        var solverJob = solverManager.solve(1L, problem);

        printSolution(solverJob.getFinalBestSolution());
        printByMachine(solverJob.getFinalBestSolution());
    }

    private ProductionSchedule loadProblem() {
        return new ProductionSchedule(List.of(
                    new Machine("M1", "PRESS"),
                    new Machine("M2", "GRIND"),
                    new Machine("M3", "PRESS"),
                    new Machine("M4", "PAINT")
                ),
                Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                List.of(
                    new Job("J1", "PRESS", 1, "O1", 1),
                    new Job("J2", "GRIND", 3, "O1", 2),
                    new Job("J3", "PAINT", 2, "O1", 3),
                    new Job("J4", "PRESS", 1, "O2", 1),
                    new Job("J5", "GRIND", 3, "O2", 2),
                    new Job("J6", "PAINT", 2, "O2", 3),
                    new Job("J7", "PRESS", 1, "O3", 1),
                    new Job("J8", "PAINT", 1, "O3", 2)
                ));
    }

    private void printSolution(ProductionSchedule productionSchedule) {
        logger.info("Job assignments");
        AsciiTable asciiTable = new AsciiTable();
        asciiTable.getRenderer().setCWC(new CWC_LongestLine().add(20, 50).add(20, 50)); // Erste und zweite Spalte auf 20 Zeichen
        asciiTable.addRule();
        asciiTable.addRow("Order", "JobId", "Machine Type", "Assigned Machine", "Start Time", "Duration", "End Time");
        asciiTable.addRule();
        for (Job job : productionSchedule.getJobList()) {
            asciiTable.addRow(job.getOrderId(), job.getId(), job.getRequiredMachineType(), job.getAssignedMachine().getId()
                    + " (" + job.getAssignedMachine().getType() + ")", job.getAssignedStartTime(), job.getDuration(), job.getEndTime());
        }
        asciiTable.addRule();
        asciiTable.setTextAlignment(TextAlignment.CENTER);
        String render = asciiTable.render();
        System.out.println(render);
    }

    private void printByMachine(ProductionSchedule productionSchedule) {
        logger.info("Jobs by machine");
        List<String> columnHeaders = new java.util.ArrayList<>(List.of("Machine", "Type"));
        columnHeaders.addAll(productionSchedule.getStartTimeRange().stream()
                .map(Object::toString)
                .toList());
        AsciiTable asciiTable = new AsciiTable();
        asciiTable.getRenderer().setCWC(new CWC_LongestLine()
                .add(10, 20)
                .add(10, 20)
                .add(7, 10)
                .add(7, 10)
                .add(7, 10)
                .add(7, 10)
                .add(7, 10)
                .add(7, 10)
                .add(7, 10)
                .add(7, 10)
                .add(7, 10)
                .add(7, 10)
        ); // Erste und zweite Spalte auf 20 Zeichen
        asciiTable.addRule();
        asciiTable.addRow(columnHeaders);
        asciiTable.addRule();
        for (Machine machine : productionSchedule.getMachineList()) {
            asciiTable.addRow(getJobsForMachineByStartTime(productionSchedule, machine));
            asciiTable.addRule();
        }
        asciiTable.setTextAlignment(TextAlignment.CENTER);
        String render = asciiTable.render();
        System.out.println(render);
    }

    private List<String> getJobsForMachineByStartTime(ProductionSchedule productionSchedule, Machine machine) {
        List<Integer> timeRange = productionSchedule.getStartTimeRange();
        String[] slots = new String[timeRange.size()];
        Arrays.fill(slots, "");

        for (Job job : productionSchedule.getJobList()) {
            if (job.getAssignedMachine() != null && job.getAssignedMachine().equals(machine) && job.getAssignedStartTime() != null) {
                int start = job.getAssignedStartTime();
                int end = start + job.getDuration();
                for (int t = start; t < end; t++) {
                    int idx = timeRange.indexOf(t);
                    if (idx >= 0) {
                        //slots[idx] = job.getOrderId() + " " + job.getIndexInOrder();
                        slots[idx] = job.getId();
                    }
                }
            }
        }

        List<String> machineJobs = new java.util.ArrayList<>();
        machineJobs.add(machine.getId());
        machineJobs.add(machine.getType());
        machineJobs.addAll(Arrays.asList(slots));
        return machineJobs;
    }
}
