package dvoraka.avservice.runner;

import dvoraka.avservice.common.runner.ServiceRunner;

import java.util.function.BooleanSupplier;

/**
 * Default runner configuration implementation.
 */
public class DefaultRunnerConfiguration implements RunnerConfiguration {

    @Override
    public String getId() {
        return null;
    }

    @Override
    public ServiceRunner getServiceRunner() {
        return null;
    }

    @Override
    public BooleanSupplier running() {
        return null;
    }
}
