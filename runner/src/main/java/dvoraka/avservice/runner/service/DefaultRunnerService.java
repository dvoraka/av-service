package dvoraka.avservice.runner.service;

import dvoraka.avservice.common.helper.ExecutorServiceHelper;
import dvoraka.avservice.runner.Runner;
import dvoraka.avservice.runner.RunnerConfiguration;
import dvoraka.avservice.runner.RunningState;
import dvoraka.avservice.runner.exception.RunnerAlreadyExistsException;
import dvoraka.avservice.runner.exception.RunnerNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Default runner service implementation.
 */
@Service
public class DefaultRunnerService implements RunnerService, ExecutorServiceHelper {

    private static final Logger log = LogManager.getLogger(DefaultRunnerService.class);

    private final ConcurrentMap<String, Runner> runners;

    private final ExecutorService executorService;


    public DefaultRunnerService() {
        runners = new ConcurrentHashMap<>();

        final int threadCount = 8;
        executorService = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public String createRunner(RunnerConfiguration configuration) throws RunnerAlreadyExistsException {

        //TODO: synchronize

        if (exists(configuration.getName())) {
            throw new RunnerAlreadyExistsException();
        }

        Runner newRunner = new Runner(configuration);
        runners.put(newRunner.getName(), newRunner);
        log.info("Created new runner with name: {}", newRunner.getName());

        return newRunner.getName();
    }

    @Override
    public List<String> listRunners() {
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
        stopAllRunners();
        shutdownAndAwaitTermination(executorService, waitTime, log);
    }

    @Override
    public void startRunner(String name) throws RunnerNotFoundException {
        checkRunnerExistence(name);
        findRunner(name).ifPresent(Runner::start);
        //TODO:
//        executorService.submit(() -> waitForStartInt(name));
    }

    @Override
    public void stopRunner(String name) throws RunnerNotFoundException {
        checkRunnerExistence(name);
        findRunner(name).ifPresent(Runner::stop);
    }

    @Override
    public long getRunnerCount() {
        return getRunners().size();
    }

    @Override
    public RunningState getRunnerState(String name) throws RunnerNotFoundException {
        return findRunner(name)
                .map(Runner::getState)
                .orElseThrow(RunnerNotFoundException::new);
    }

    private void waitForStartInt(String name) {
        try {
            waitForStart(name);
        } catch (RunnerNotFoundException e) {
            log.warn("Runner {} not found!", name);
        } catch (InterruptedException e) {
            log.warn("Waiting for {} start interrupted!", name);
        }
    }

    @Override
    public void waitForStart(String name) throws RunnerNotFoundException, InterruptedException {
        Runner runner = findRunner(name).orElseThrow(RunnerNotFoundException::new);

        final int sleepTime = 250;
        while (!runner.isRunning()) {
            TimeUnit.MILLISECONDS.sleep(sleepTime);
        }

        runner.setState(RunningState.RUNNING);
    }

    private Optional<Runner> findRunner(String name) {
        return Optional.ofNullable(getRunners().get(name));
    }

    private void checkRunnerExistence(String name) throws RunnerNotFoundException {
        if (!exists(name)) {
            throw new RunnerNotFoundException();
        }
    }

    private boolean exists(String name) {
        return getRunners().containsKey(name);
    }

    private Map<String, Runner> getRunners() {
        return runners;
    }

    private void stopAllRunners() {
        log.info("Stopping all runners...");

        for (String runnerName : listRunners()) {
            try {
                log.debug("Stopping {}...", runnerName);
                stopRunner(runnerName);
            } catch (RunnerNotFoundException e) {
                log.warn("Runner {} not found!", runnerName);
            }
        }

        log.info("Stopping done.");
    }
}
