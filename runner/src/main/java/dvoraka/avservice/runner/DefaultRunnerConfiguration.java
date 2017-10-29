package dvoraka.avservice.runner;

import dvoraka.avservice.common.runner.ServiceRunner;

import java.util.function.BooleanSupplier;

/**
 * Default runner configuration implementation.
 */
public class DefaultRunnerConfiguration implements RunnerConfiguration {

    private final long configurationId;
    private final ServiceRunner serviceRunner;
    private final BooleanSupplier supplier;


    public DefaultRunnerConfiguration(
            long configurationId,
            ServiceRunner serviceRunner,
            BooleanSupplier supplier
    ) {
        this.configurationId = configurationId;
        this.serviceRunner = serviceRunner;
        this.supplier = supplier;
    }

    @Override
    public long getId() {
        return configurationId;
    }

    @Override
    public String getName() {
        return null;
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
