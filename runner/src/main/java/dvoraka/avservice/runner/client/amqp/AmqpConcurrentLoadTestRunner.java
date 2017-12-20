package dvoraka.avservice.runner.client.amqp;

import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.client.perf.ConcurrentPerformanceTester;
import dvoraka.avservice.common.runner.AbstractAppRunner;
import dvoraka.avservice.common.runner.AppRunner;
import dvoraka.avservice.common.service.ApplicationManagement;

/**
 * AMQP concurrent load test runner.
 */
public class AmqpConcurrentLoadTestRunner extends AbstractAppRunner {

    public static void main(String[] args) {
        AppRunner runner = new AmqpConcurrentLoadTestRunner();
        runner.run();
    }

    @Override
    protected String[] profiles() {
        return new String[]{"client", "performance", "concurrent", "amqp", "file-client", "no-db"};
    }

    @Override
    protected Class<?>[] configClasses() {
        return new Class<?>[]{ClientConfig.class};
    }

    @Override
    protected Class<? extends ApplicationManagement> runClass() {
        return ConcurrentPerformanceTester.class;
    }
}
