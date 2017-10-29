package dvoraka.avservice.runner;

import dvoraka.avservice.common.runner.ServiceRunner;

import java.util.function.BooleanSupplier;

/**
 * Runner configuration.
 */
public interface RunnerConfiguration {

    /**
     * Returns a configuration ID. Usually assigned by the runner service.
     *
     * @return the configuration ID
     */
    long getId();

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
     * Returns a boolean supplier for checking a service running status.
     *
     * @return the boolean supplier
     */
    BooleanSupplier running();
}
