package dvoraka.avservice.runner.service;

import dvoraka.avservice.common.helper.ExecutorServiceHelper;
import dvoraka.avservice.runner.Runner;
import dvoraka.avservice.runner.RunnerAlreadyExistsException;
import dvoraka.avservice.runner.RunnerNotFoundException;
import dvoraka.avservice.runner.RunningState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
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
    private final AtomicLong runnerCount;

    private final ExecutorService executorService;


    public DefaultRunnerService() {
        runners = new ConcurrentHashMap<>();
        runnerCount = new AtomicLong();

        final int threadCount = 8;
        executorService = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public long createRunner(Runner configuration) throws RunnerAlreadyExistsException {

        // create runner

        log.info("Created new runner with ID: {}...", runnerCount.get());

        if (runners.containsKey(configuration.getId())) {
            throw new RunnerAlreadyExistsException();
        }

        states.put(configuration.getId(), RunningState.UNKNOWN);
        runners.put(configuration.getId(), configuration);

        return 0;
    }

    @Override
    public ArrayList<E> listRunners() {
        return new ArrayList<>(runners.keySet());
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
    public void startRunner(Long id) throws RunnerNotFoundException {
        checkRunnerExistence(id);

        Runner configuration = runners.get(id);
        configuration.getServiceRunner().runAsync();
        states.put(id, RunningState.STARTING);

        executorService.execute(() -> updateState(id));
    }

    @Override
    public void stopRunner(long id) throws RunnerNotFoundException {

    }

    @Override
    public void stopRunner(Long id) throws RunnerNotFoundException {
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
