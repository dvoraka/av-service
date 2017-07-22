package dvoraka.avservice.runner;

import dvoraka.avservice.common.runner.ServiceRunner;

import java.util.function.BooleanSupplier;

public class DummyRunnerConfiguration implements RunnerConfiguration {

    @Override
    public String getId() {
        return "dummy";
    }

    @Override
    public ServiceRunner getServiceRunner() {
        return new ServiceRunner() {
            @Override
            public void stop() {
            }

            @Override
            public void run() {
            }

            @Override
            public void runAsync() {
            }

            @Override
            public boolean isRunning() {
                return false;
            }
        };
    }

    @Override
    public BooleanSupplier running() {
        return () -> true;
    }
}
