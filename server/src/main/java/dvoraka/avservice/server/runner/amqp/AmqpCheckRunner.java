package dvoraka.avservice.server.runner.amqp;

import dvoraka.avservice.client.checker.CheckApp;
import dvoraka.avservice.common.runner.AbstractAppRunner;
import dvoraka.avservice.common.runner.AppRunner;
import dvoraka.avservice.common.service.ApplicationManagement;
import dvoraka.avservice.server.configuration.amqp.AmqpConfig;

import java.io.IOException;

/**
 * AMQP check runner.
 */
public class AmqpCheckRunner extends AbstractAppRunner {

    public static void main(String[] args) throws IOException {
        AppRunner runner = new AmqpCheckRunner();
        runner.run();
    }

    @Override
    protected String[] profiles() {
        return new String[]{"client", "amqp", "amqp-client", "checker", "no-db"};
    }

    @Override
    protected Class<?>[] configClasses() {
        return new Class<?>[]{AmqpConfig.class};
    }

    @Override
    protected Class<? extends ApplicationManagement> runClass() {
        return CheckApp.class;
    }
}
