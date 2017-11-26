package dvoraka.avservice.runner;

import dvoraka.avservice.common.runner.ServiceRunner;

import java.util.function.BooleanSupplier;

import static java.util.Objects.requireNonNull;

/**
 * Default runner configuration implementation.
 */
public class DefaultRunnerConfiguration implements RunnerConfiguration {

    private final String name;
    private final ServiceRunner serviceRunner;
    private final BooleanSupplier supplier;


    public DefaultRunnerConfiguration(
            String name,
            ServiceRunner serviceRunner,
            BooleanSupplier supplier
    ) {
        this.name = requireNonNull(name);
        this.serviceRunner = requireNonNull(serviceRunner);
        this.supplier = supplier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ServiceRunner getServiceRunner() {
        return serviceRunner;
    }

    @Override
    public BooleanSupplier running() {
        return supplier;
    }
}
