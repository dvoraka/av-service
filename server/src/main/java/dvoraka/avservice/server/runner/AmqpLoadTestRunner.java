package dvoraka.avservice.server.runner;

import dvoraka.avservice.common.runner.AbstractRunner;
import dvoraka.avservice.common.runner.Runner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.checker.DefaultPerformanceTester;
import dvoraka.avservice.server.configuration.amqp.AmqpConfig;

import java.io.IOException;

/**
 * AMQP load test runner.
 */
public class AmqpLoadTestRunner extends AbstractRunner {

    public static void main(String[] args) throws IOException {
        Runner runner = new AmqpLoadTestRunner();
        runner.run();
    }

    @Override
    protected String[] profiles() {
        return new String[]{"amqp", "amqp-checker", "no-db"};
    }

    @Override
    protected Class<?>[] configClasses() {
        return new Class<?>[]{AmqpConfig.class};
    }

    @Override
    protected Class<? extends ServiceManagement> runClass() {
        return DefaultPerformanceTester.class;
    }

    @Override
    protected void waitForKey() throws IOException {
    }
}
