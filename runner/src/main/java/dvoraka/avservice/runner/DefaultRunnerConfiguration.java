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
    private BooleanSupplier checker;


    public DefaultRunnerConfiguration(
            String name,
            ServiceRunner serviceRunner,
            BooleanSupplier checker
    ) {
        this.name = requireNonNull(name);
        this.serviceRunner = requireNonNull(serviceRunner);
        this.checker = checker;
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
    public BooleanSupplier getChecker() {
        return checker;
    }

    @Override
    public void updateChecker(BooleanSupplier checker) {
        this.checker = requireNonNull(checker);
    }
}
