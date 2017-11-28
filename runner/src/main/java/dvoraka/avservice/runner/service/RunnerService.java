package dvoraka.avservice.runner.service;

import dvoraka.avservice.runner.RunnerConfiguration;
import dvoraka.avservice.runner.RunningState;
import dvoraka.avservice.runner.exception.RunnerAlreadyExistsException;
import dvoraka.avservice.runner.exception.RunnerNotFoundException;

import java.util.List;

/**
 * Runner service interface. Abstraction for easier service runner usage.
 *
 * @see dvoraka.avservice.common.runner.ServiceRunner
 */
public interface RunnerService {

    /**
     * Creates a runner and returns the runner name.
     *
     * @param configuration the runner configuration
     * @return the runner name
     * @throws RunnerAlreadyExistsException if runner with the same name already exists
     */
    String createRunner(RunnerConfiguration configuration) throws RunnerAlreadyExistsException;

    /**
     * Lists names of all service runners.
     *
     * @return the list of names
     */
    List<String> listRunners();

    /**
     * Returns existence of a runner.
     *
     * @param runnerName the runner name
     * @return true if exists
     */
    boolean exists(String runnerName);

    /**
     * Stats the service.
     */
    void start();

    /**
     * Stops the service.
     */
    void stop();

    void startRunner(String name) throws RunnerNotFoundException;

    void stopRunner(String name) throws RunnerNotFoundException;

    long getRunnerCount();

    RunningState getRunnerState(String name) throws RunnerNotFoundException;

    /**
     * Blocks until a runner is running.
     *
     * @param name the runner name
     */
    void waitForStart(String name) throws RunnerNotFoundException, InterruptedException;
}
