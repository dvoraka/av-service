package dvoraka.avservice.runner.service;

import dvoraka.avservice.runner.RunnerAlreadyExistsException;
import dvoraka.avservice.runner.RunnerConfiguration;
import dvoraka.avservice.runner.RunnerNotFoundException;
import dvoraka.avservice.runner.RunningState;

import java.util.Collection;

/**
 * Runner service interface.
 */
public interface RunnerService {

    void createRunner(RunnerConfiguration configuration) throws RunnerAlreadyExistsException;

    Collection<String> listRunners();

    void start();

    void stop();

    void startRunner(String id) throws RunnerNotFoundException;

    void stopRunner(String id) throws RunnerNotFoundException;

    RunningState getRunnerState(String id) throws RunnerNotFoundException;
}
