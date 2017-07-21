package dvoraka.avservice.runner.service;

import dvoraka.avservice.runner.RunnerAlreadyExistsException;
import dvoraka.avservice.runner.RunnerConfiguration;
import dvoraka.avservice.runner.RunnerNotFoundException;
import dvoraka.avservice.runner.RunningState;

/**
 * Runner service interface.
 */
public interface RunnerService {

    void create(RunnerConfiguration configuration) throws RunnerAlreadyExistsException;

    void start(String id) throws RunnerNotFoundException;

    void stop(String id) throws RunnerNotFoundException;

    RunningState getState(String id) throws RunnerNotFoundException;
}
