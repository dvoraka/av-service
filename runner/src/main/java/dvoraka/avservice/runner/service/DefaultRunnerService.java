package dvoraka.avservice.runner.service;

import dvoraka.avservice.common.helper.ExecutorServiceHelper;
import dvoraka.avservice.runner.Runner;
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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default runner service implementation.
 */
@Service
public class DefaultRunnerService implements RunnerService, ExecutorServiceHelper {

    private static final Logger log = LogManager.getLogger(DefaultRunnerService.class);

    private final ConcurrentMap<Long, Runner> runners;
    private final AtomicLong runnerCounter;

    private final ExecutorService executorService;


    public DefaultRunnerService() {
        runners = new ConcurrentHashMap<>();
        runnerCounter = new AtomicLong();

        final int threadCount = 8;
        executorService = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public long createRunner(RunnerConfiguration configuration) throws RunnerAlreadyExistsException {

        if (runners.values().stream()
                .map(Runner::getConfiguration)
                .map(RunnerConfiguration::getName)
                .anyMatch(name -> name.equals(configuration.getName()))) {

            throw new RunnerAlreadyExistsException();
        }

        Runner newRunner = new Runner(configuration);
        long id = runnerCounter.getAndIncrement();
        runners.put(id, newRunner);
        log.info("Created new runner with ID: {}...", id);

        return id;
    }

    @Override
    public List<Runner> listRunners() {
        return new ArrayList<>(runners.values());
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
    public void startRunner(long id) throws RunnerNotFoundException {

    }

    @Override
    public void startRunner(String id) throws RunnerNotFoundException {
//        checkRunnerExistence(id);

        Runner configuration = runners.get(id);
        configuration.getServiceRunner().runAsync();
        states.put(id, RunningState.STARTING);

        executorService.execute(() -> updateState(id));
    }

    @Override
    public void stopRunner(long id) throws RunnerNotFoundException {

    }

    @Override
    public void stopRunner(String id) throws RunnerNotFoundException {
        checkRunnerExistence(id);

        Runner configuration = runners.get(id);
        configuration.getServiceRunner().stop();
        states.put(id, RunningState.STOPPED);
    }

    @Override
    public RunningState getRunnerState(Long id) throws RunnerNotFoundException {
        checkRunnerExistence(id);

        return states.get(id);
    }

    private void updateState(Long id) {
        Runner configuration = runners.get(id);

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

    private void checkRunnerExistence(Long id) throws RunnerNotFoundException {
        if (!runners.containsKey(id)) {
            throw new RunnerNotFoundException();
        }
    }
}
