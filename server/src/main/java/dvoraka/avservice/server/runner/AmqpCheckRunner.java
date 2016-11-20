package dvoraka.avservice.server.runner;

import dvoraka.avservice.common.runner.AbstractRunner;
import dvoraka.avservice.common.runner.Runner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.checker.CheckApp;
import dvoraka.avservice.server.configuration.amqp.AmqpConfig;

import java.io.IOException;

/**
 * AMQP check runner.
 */
public class AmqpCheckRunner extends AbstractRunner {

    public static void main(String[] args) throws IOException {
        Runner runner = new AmqpCheckRunner();
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
        return CheckApp.class;
    }

    @Override
    protected void waitForKey() throws IOException {
        // no waiting
    }
}
