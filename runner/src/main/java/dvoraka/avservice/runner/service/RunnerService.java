package dvoraka.avservice.runner.service;

import dvoraka.avservice.runner.RunnerAlreadyExists;
import dvoraka.avservice.runner.RunnerConfiguration;
import dvoraka.avservice.runner.RunningState;

/**
 * Runner service interface.
 */
public interface RunnerService {

    void create(RunnerConfiguration configuration) throws RunnerAlreadyExists;

    void start(String id);

    void stop(String id);

    RunningState getState(String id);
}
