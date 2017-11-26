package dvoraka.avservice.runner;

import dvoraka.avservice.common.runner.ServiceRunner;

import java.util.function.BooleanSupplier;

/**
 * Runner configuration.
 */
public interface RunnerConfiguration {

    /**
     * Returns a configuration name.
     *
     * @return the configuration name
     */
    String getName();

    /**
     * Returns a service runner.
     *
     * @return the service runner
     * @see ServiceRunner
     */
    ServiceRunner getServiceRunner();

    /**
     * Returns a checker for checking a service running status.
     *
     * @return the checker
     */
    BooleanSupplier getChecker();

    /**
     * Updates a checker.
     *
     * @param checker new checker
     */
    void updateChecker(BooleanSupplier checker);
}
