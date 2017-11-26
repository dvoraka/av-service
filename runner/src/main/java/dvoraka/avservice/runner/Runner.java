package dvoraka.avservice.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BooleanSupplier;

import static java.util.Objects.requireNonNull;

public class Runner {

    private static final Logger log = LogManager.getLogger(Runner.class);

    private final RunnerConfiguration configuration;

    private RunningState state;


    public Runner(RunnerConfiguration configuration) {
        this.configuration = requireNonNull(configuration);

        this.state = RunningState.NEW;
    }

    public RunnerConfiguration getConfiguration() {
        return configuration;
    }

    public String getName() {
        return getConfiguration().getName();
    }

    public RunningState getState() {
        return state;
    }

    public void setState(RunningState state) {
        this.state = state;
    }

    public void start() {
        if (!(getState() == RunningState.NEW || getState() == RunningState.STOPPED)) {
            log.info("Runner already started.");

            return;
        }

        log.info("Starting runner...");
        getConfiguration().getServiceRunner().runAsync();

        setState(RunningState.STARTING);
    }

    public void stop() {
        getConfiguration().getServiceRunner().stop();
        setState(RunningState.STOPPED);
    }

    public boolean isRunning() {
        BooleanSupplier checker = getConfiguration().getChecker();

        return checker != null && checker.getAsBoolean();
    }
}
