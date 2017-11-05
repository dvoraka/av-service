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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

        long id = runnerCounter.getAndIncrement();
        Runner newRunner = new Runner(configuration, id);
        runners.put(id, newRunner);
        log.info("Created new runner with ID: {}...", id);

        return id;
    }

    @Override
    public List<String> listRunners() {
        return runners.values().stream()
                .map(Runner::getName)
                .collect(Collectors.toList());
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
        checkRunnerExistence(id);
        findRunner(id).ifPresent(Runner::start);
    }

    @Override
    public void startRunner(String name) throws RunnerNotFoundException {
        checkRunnerExistence(name);
        findRunner(name).ifPresent(Runner::start);
    }

    @Override
    public void stopRunner(long id) throws RunnerNotFoundException {
        checkRunnerExistence(id);
    }

    @Override
    public void stopRunner(String name) throws RunnerNotFoundException {
        checkRunnerExistence(name);
    }

    @Override
    public RunningState getRunnerState(Long id) throws RunnerNotFoundException {
        return findRunner(id)
                .map(Runner::getState)
                .orElseThrow(RunnerNotFoundException::new);
    }

    private Optional<Runner> findRunner(long id) {
        return Optional.ofNullable(getRunners().get(id));
    }

    private Optional<Runner> findRunner(String name) {
        return findRunnerId(name)
                .map(id -> getRunners().get(id));
    }

    private Optional<Long> findRunnerId(String name) {
        return runners.values().stream()
                .filter(runner -> runner.getName().equals(name))
                .findFirst()
                .map(Runner::getId);
    }

    private void updateState(Long id) {
//        Runner configuration = runners.get(id);
//
//        final int waitTime = 1_000;
//        while (!configuration.running().getAsBoolean()) {
//            try {
//                Thread.sleep(waitTime);
//            } catch (InterruptedException e) {
//                log.warn("Waiting interrupted!", e);
//                Thread.currentThread().interrupt();
//
//                return;
//            }
//        }

//        states.put(id, RunningState.RUNNING);
    }

    private void checkRunnerExistence(Long id) throws RunnerNotFoundException {
        if (!runners.containsKey(id)) {
            throw new RunnerNotFoundException();
        }
    }

    private void checkRunnerExistence(String name) throws RunnerNotFoundException {
        if (runners.values().stream()
                .map(Runner::getConfiguration)
                .map(RunnerConfiguration::getName)
                .noneMatch(runnerName -> runnerName.equals(name))) {

            throw new RunnerNotFoundException();
        }
    }

    private ConcurrentMap<Long, Runner> getRunners() {
        return runners;
    }
}
