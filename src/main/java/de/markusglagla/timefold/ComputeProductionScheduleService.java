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
        var solution = solverManager.solve(1L, problem);

        printSolution(solution.getFinalBestSolution());
        printByMachine(solution.getFinalBestSolution());
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
                    new Operation("O1", "PRESS", 1, "J1", 1),
                    new Operation("O2", "GRIND", 3, "J1", 2),
                    new Operation("O3", "PAINT", 2, "J1", 3),
                    new Operation("O4", "PRESS", 1, "J2", 1),
                    new Operation("O5", "GRIND", 3, "J2", 2),
                    new Operation("O6", "PAINT", 2, "J2", 3),
                    new Operation("O7", "PRESS", 1, "J3", 1),
                    new Operation("O8", "PAINT", 1, "J3", 2)
                ));
    }

    private void printSolution(ProductionSchedule productionSchedule) {
        logger.info("Job assignments");
        AsciiTable asciiTable = new AsciiTable();
        asciiTable.getRenderer().setCWC(new CWC_LongestLine().add(20, 50).add(20, 50)); // Erste und zweite Spalte auf 20 Zeichen
        asciiTable.addRule();
        asciiTable.addRow("Job", "Operation", "Machine Type", "Assigned Machine", "Start Time", "Duration", "End Time");
        asciiTable.addRule();
        for (Operation op : productionSchedule.getOperationList()) {
            asciiTable.addRow(op.getJobId(), op.getId(), op.getRequiredMachineType(), op.getAssignedMachine().getId(), op.getAssignedStartTime(), op.getDuration(), op.getEndTime());
        }
        asciiTable.addRule();
        asciiTable.setTextAlignment(TextAlignment.CENTER);
        String render = asciiTable.render();
        System.out.println(render);
    }

    private void printByMachine(ProductionSchedule productionSchedule) {
        logger.info("Operations by machine");
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
            asciiTable.addRow(getOperationsForMachineByStartTime(productionSchedule, machine));
            asciiTable.addRule();
        }
        asciiTable.setTextAlignment(TextAlignment.CENTER);
        String render = asciiTable.render();
        System.out.println(render);
    }

    private List<String> getOperationsForMachineByStartTime(ProductionSchedule productionSchedule, Machine machine) {
        List<Integer> timeRange = productionSchedule.getStartTimeRange();
        String[] slots = new String[timeRange.size()];
        Arrays.fill(slots, "");

        for (Operation op : productionSchedule.getOperationList()) {
            if (op.getAssignedMachine() != null && op.getAssignedMachine().equals(machine) && op.getAssignedStartTime() != null) {
                int start = op.getAssignedStartTime();
                int end = start + op.getDuration();
                for (int t = start; t < end; t++) {
                    int idx = timeRange.indexOf(t);
                    if (idx >= 0) {
                        slots[idx] = op.getJobId() + " " + op.getIndexInJob();
                    }
                }
            }
        }

        List<String> machineOperations = new java.util.ArrayList<>();
        machineOperations.add(machine.getId());
        machineOperations.add(machine.getType());
        machineOperations.addAll(Arrays.asList(slots));
        return machineOperations;
    }
}
