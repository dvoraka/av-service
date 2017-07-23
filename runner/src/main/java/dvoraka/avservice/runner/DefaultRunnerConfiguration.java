package dvoraka.avservice.runner;

import dvoraka.avservice.common.runner.ServiceRunner;

import java.util.function.BooleanSupplier;

/**
 * Default runner configuration implementation.
 */
public class DefaultRunnerConfiguration implements RunnerConfiguration {

    private final String configurationId;
    private final ServiceRunner serviceRunner;
    private final BooleanSupplier supplier;


    public DefaultRunnerConfiguration(
            String configurationId,
            ServiceRunner serviceRunner,
            BooleanSupplier supplier
    ) {
        this.configurationId = configurationId;
        this.serviceRunner = serviceRunner;
        this.supplier = supplier;
    }

    @Override
    public String getId() {
        return configurationId;
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
