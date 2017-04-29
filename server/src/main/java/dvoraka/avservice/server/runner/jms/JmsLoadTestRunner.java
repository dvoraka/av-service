package dvoraka.avservice.server.runner.jms;

import dvoraka.avservice.client.checker.PerformanceTester;
import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.common.runner.AbstractAppRunner;
import dvoraka.avservice.common.runner.AppRunner;
import dvoraka.avservice.common.service.ApplicationManagement;

import java.io.IOException;

/**
 * JMS load test runner.
 */
public class JmsLoadTestRunner extends AbstractAppRunner {

    public static void main(String[] args) throws IOException {
        AppRunner runner = new JmsLoadTestRunner();
        runner.run();
    }

    @Override
    protected String[] profiles() {
        return new String[]{"client", "checker", "jms", "file-client", "no-db"};
    }

    @Override
    protected Class<?>[] configClasses() {
        return new Class<?>[]{ClientConfig.class};
    }

    @Override
    protected Class<? extends ApplicationManagement> runClass() {
        return PerformanceTester.class;
    }
}
