package dvoraka.avservice.runner.service;

import dvoraka.avservice.runner.RunningState;
import dvoraka.avservice.runner.exception.RunnerAlreadyExistsException;
import dvoraka.avservice.runner.exception.RunnerNotFoundException;
import dvoraka.avservice.runner.runnerconfiguration.RunnerConfiguration;

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
     * Lists names of all runners.
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

    /**
     * Starts a runner with a given name.
     *
     * @param name the runner name
     * @throws RunnerNotFoundException if runner doesn't exist
     */
    void startRunner(String name) throws RunnerNotFoundException;

    /**
     * Stops a runner with a given name.
     *
     * @param name the runner name
     * @throws RunnerNotFoundException if runner doesn't exist
     */
    void stopRunner(String name) throws RunnerNotFoundException;

    /**
     * Returns a runner count.
     *
     * @return the runner count
     */
    long getRunnerCount();

    /**
     * Returns a runner state.
     *
     * @param name the runner name
     * @return the state
     * @throws RunnerNotFoundException if runner doesn't exist
     */
    RunningState getRunnerState(String name) throws RunnerNotFoundException;

    /**
     * Blocks until a runner is running.
     *
     * @param name the runner name
     * @throws RunnerNotFoundException if runner doesn't exist
     * @throws InterruptedException    if waiting is interrupted
     */
    void waitForStart(String name) throws RunnerNotFoundException, InterruptedException;
}
