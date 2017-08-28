package dvoraka.avservice.runner.service;

import dvoraka.avservice.common.helper.ExecutorServiceHelper;
import dvoraka.avservice.runner.RunnerAlreadyExistsException;
import dvoraka.avservice.runner.RunnerConfiguration;
import dvoraka.avservice.runner.RunnerNotFoundException;
import dvoraka.avservice.runner.RunningState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Default runner service implementation.
 */
@Service
public class DefaultRunnerService implements RunnerService, ExecutorServiceHelper {

    private static final Logger log = LogManager.getLogger(DefaultRunnerService.class);

    private final ConcurrentMap<String, RunnerConfiguration> configurations;
    private final ConcurrentMap<String, RunningState> states;
    private final ExecutorService executorService;


    public DefaultRunnerService() {
        configurations = new ConcurrentHashMap<>();
        states = new ConcurrentHashMap<>();

        final int threadCount = 8;
        executorService = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public void createRunner(RunnerConfiguration configuration)
            throws RunnerAlreadyExistsException {
        log.info("Creating new configuration: {}...", configuration.getId());

        if (configurations.containsKey(configuration.getId())) {
            throw new RunnerAlreadyExistsException();
        }

        states.put(configuration.getId(), RunningState.UNKNOWN);
        configurations.put(configuration.getId(), configuration);
    }

    @Override
    public Collection<String> listRunners() {
        return new ArrayList<>(configurations.keySet());
    }

    @Override
    @PostConstruct
    public void start() {
        // do nothing for now
    }

    @Override
    @PreDestroy
    public void stop() {
        final int waitTime = 5;
        shutdownAndAwaitTermination(executorService, waitTime, log);
    }

    @Override
    public void startRunner(String id) throws RunnerNotFoundException {
        checkRunnerExistence(id);

        RunnerConfiguration configuration = configurations.get(id);
        configuration.getServiceRunner().runAsync();
        states.put(id, RunningState.STARTING);

        executorService.execute(() -> updateState(id));
    }

    @Override
    public void stopRunner(String id) throws RunnerNotFoundException {
        checkRunnerExistence(id);

        RunnerConfiguration configuration = configurations.get(id);
        configuration.getServiceRunner().stop();
        states.put(id, RunningState.STOPPED);
    }

    @Override
    public RunningState getRunnerState(String id) throws RunnerNotFoundException {
        checkRunnerExistence(id);

        return states.get(id);
    }

    private void updateState(String id) {
        RunnerConfiguration configuration = configurations.get(id);

        final int waitTime = 1_000;
        while (!configuration.running().getAsBoolean()) {
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                log.warn("Waiting interrupted!", e);
                Thread.currentThread().interrupt();

                return;
            }
        }

        states.put(id, RunningState.RUNNING);
    }

    private void checkRunnerExistence(String id) throws RunnerNotFoundException {
        if (!configurations.containsKey(id)) {
            throw new RunnerNotFoundException();
        }
    }
}
