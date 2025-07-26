package de.markusglagla.timefold;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TimefoldDemoApplication implements CommandLineRunner {

    private final ComputeProductionScheduleService computeProductionScheduleService;
    private final ApplicationContext context;

    public TimefoldDemoApplication(ComputeProductionScheduleService computeProductionScheduleService, ApplicationContext context) {
        this.computeProductionScheduleService = computeProductionScheduleService;
        this.context = context;
    }

    public static void main(String[] args) {
        SpringApplication.run(TimefoldDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        computeProductionScheduleService.compute();
        SpringApplication.exit(context, () -> 0);
    }
}
