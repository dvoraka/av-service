package dvoraka.avservice.runner.service;

import dvoraka.avservice.runner.RunnerAlreadyExistsException;
import dvoraka.avservice.runner.RunnerConfiguration;
import dvoraka.avservice.runner.RunnerNotFoundException;
import dvoraka.avservice.runner.RunningState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Default runner service implementation.
 */
@Service
public class DefaultRunnerService implements RunnerService {

    private final ConcurrentMap<String, RunnerConfiguration> configurations;

    private static final Logger log = LogManager.getLogger(DefaultRunnerService.class);

    private final ConcurrentMap<String, RunningState> states;


    public DefaultRunnerService() {
        configurations = new ConcurrentHashMap<>();
        states = new ConcurrentHashMap<>();
    }

    @Override
    public void create(RunnerConfiguration configuration) throws RunnerAlreadyExistsException {
        log.info("Creating new configuration: {}...", configuration.getId());

        if (configurations.containsKey(configuration.getId())) {
            throw new RunnerAlreadyExistsException();
        }

        states.put(configuration.getId(), RunningState.UNKNOWN);
        configurations.put(configuration.getId(), configuration);
    }

    @Override
    public void start(String id) throws RunnerNotFoundException {
        checkRunnerExistence(id);
    }

    @Override
    public void stop(String id) throws RunnerNotFoundException {
        checkRunnerExistence(id);
    }

    @Override
    public RunningState getState(String id) throws RunnerNotFoundException {
        checkRunnerExistence(id);

        return states.get(id);
    }

    private void checkRunnerExistence(String id) throws RunnerNotFoundException {
        if (!configurations.containsKey(id)) {
            throw new RunnerNotFoundException();
        }
    }
}
