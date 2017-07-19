package dvoraka.avservice.runner.service;

import dvoraka.avservice.runner.RunnerConfiguration;
import dvoraka.avservice.runner.RunningState;

/**
 * Runner service interface.
 */
public interface RunnerService {

    void start(RunnerConfiguration configuration);

    void start(String id);

    void stop(RunnerConfiguration configuration);

    void stop(String id);

    RunningState getState(RunnerConfiguration configuration);

    RunningState getState(String id);
}
