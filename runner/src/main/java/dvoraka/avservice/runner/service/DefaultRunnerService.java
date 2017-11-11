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
import java.util.Map;
import java.util.Optional;
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
        log.info("Created new runner with name: {}...", newRunner.getName());

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
        shutdownAndAwaitTermination(executorService, waitTime, log);
    }

    @Override
    public void startRunner(String name) throws RunnerNotFoundException {
        checkRunnerExistence(name);
        findRunner(name).ifPresent(Runner::start);
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

    @Override
    public void waitForRunner(String name) {
        //TODO
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
}
