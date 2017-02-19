package dvoraka.avservice.server.runner.jms;

import dvoraka.avservice.common.runner.AbstractAppRunner;
import dvoraka.avservice.common.runner.AppRunner;
import dvoraka.avservice.common.service.ApplicationManagement;
import dvoraka.avservice.server.checker.DefaultPerformanceTester;
import dvoraka.avservice.server.configuration.jms.JmsConfig;

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
        return new String[]{"jms", "jms-checker", "no-db"};
    }

    @Override
    protected Class<?>[] configClasses() {
        return new Class<?>[]{JmsConfig.class};
    }

    @Override
    protected Class<? extends ApplicationManagement> runClass() {
        return DefaultPerformanceTester.class;
    }
}
