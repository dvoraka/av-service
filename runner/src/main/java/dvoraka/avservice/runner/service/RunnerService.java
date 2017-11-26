package dvoraka.avservice.runner.service;

import dvoraka.avservice.runner.RunnerConfiguration;
import dvoraka.avservice.runner.RunningState;
import dvoraka.avservice.runner.exception.RunnerAlreadyExistsException;
import dvoraka.avservice.runner.exception.RunnerNotFoundException;

import java.util.List;

/**
 * Runner service interface.
 */
public interface RunnerService {

    /**
     * Creates a runner and returns the runner name.
     *
     * @param configuration the runner configuration
     * @return the runner name
     * @throws RunnerAlreadyExistsException if runner already exists
     */
    String createRunner(RunnerConfiguration configuration) throws RunnerAlreadyExistsException;

    List<String> listRunners();

    void start();

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
