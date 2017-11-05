package dvoraka.avservice.runner.service;

import dvoraka.avservice.runner.RunnerAlreadyExistsException;
import dvoraka.avservice.runner.RunnerConfiguration;
import dvoraka.avservice.runner.RunnerNotFoundException;
import dvoraka.avservice.runner.RunningState;

import java.util.List;

/**
 * Runner service interface.
 */
public interface RunnerService {

    /**
     * Creates a runner and returns the runner ID.
     *
     * @param configuration the runner configuration
     * @return the runner ID
     * @throws RunnerAlreadyExistsException if runner already exists
     */
    long createRunner(RunnerConfiguration configuration) throws RunnerAlreadyExistsException;

    List<String> listRunners();

    void start();

    void stop();

    void startRunner(long id) throws RunnerNotFoundException;

    void startRunner(String name) throws RunnerNotFoundException;

    void stopRunner(long id) throws RunnerNotFoundException;

    void stopRunner(String name) throws RunnerNotFoundException;

    RunningState getRunnerState(Long id) throws RunnerNotFoundException;
}
