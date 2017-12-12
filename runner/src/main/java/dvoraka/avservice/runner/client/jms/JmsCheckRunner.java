package dvoraka.avservice.runner.client.jms;

import dvoraka.avservice.client.checker.CheckApp;
import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.common.runner.AbstractAppRunner;
import dvoraka.avservice.common.runner.AppRunner;
import dvoraka.avservice.common.service.ApplicationManagement;

/**
 * JMS check runner.
 */
public class JmsCheckRunner extends AbstractAppRunner {

    public static void main(String[] args) {
        AppRunner runner = new JmsCheckRunner();
        runner.run();
    }

    @Override
    protected String[] profiles() {
        return new String[]{"client", "jms", "file-client", "checker", "no-db"};
    }

    @Override
    protected Class<?>[] configClasses() {
        return new Class<?>[]{ClientConfig.class};
    }

    @Override
    protected Class<? extends ApplicationManagement> runClass() {
        return CheckApp.class;
    }
}
