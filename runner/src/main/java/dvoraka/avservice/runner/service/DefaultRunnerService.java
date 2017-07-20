package dvoraka.avservice.runner.service;

import dvoraka.avservice.runner.RunnerAlreadyExists;
import dvoraka.avservice.runner.RunnerConfiguration;
import dvoraka.avservice.runner.RunningState;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Default runner service implementation.
 */
@Service
public class DefaultRunnerService implements RunnerService {

    private final ConcurrentMap<String, RunnerConfiguration> configurations;


    public DefaultRunnerService() {
        configurations = new ConcurrentHashMap<>();
    }

    @Override
    public void create(RunnerConfiguration configuration) throws RunnerAlreadyExists {
        if (configurations.containsKey(configuration.getId())) {
            throw new RunnerAlreadyExists();
        }

        configurations.put(configuration.getId(), configuration);
    }

    @Override
    public void start(String id) {
    }

    @Override
    public void stop(String id) {
    }

    @Override
    public RunningState getState(String id) {
        return null;
    }
}
