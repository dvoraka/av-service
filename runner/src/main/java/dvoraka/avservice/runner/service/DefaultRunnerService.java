package dvoraka.avservice.runner.service;

import dvoraka.avservice.runner.RunnerAlreadyExists;
import dvoraka.avservice.runner.RunnerConfiguration;
import dvoraka.avservice.runner.RunningState;

/**
 * Default runner service implementation.
 */
public class DefaultRunnerService implements RunnerService {

    @Override
    public void create(RunnerConfiguration configuration) throws RunnerAlreadyExists {
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
