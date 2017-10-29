package dvoraka.avservice.runner;

import dvoraka.avservice.common.runner.ServiceRunner;

import java.util.function.BooleanSupplier;

/**
 * Dummy runner configuration which does nothing.
 */
public class DummyRunnerConfiguration implements RunnerConfiguration {

    private boolean running;


    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public ServiceRunner getServiceRunner() {
        return new ServiceRunner() {
            @Override
            public void stop() {
                running = false;
            }

            @Override
            public void run() {
                running = true;
            }

            @Override
            public void runAsync() {
                run();
            }

            @Override
            public boolean isRunning() {
                return running;
            }
        };
    }

    @Override
    public BooleanSupplier running() {
        return () -> true;
    }
}
