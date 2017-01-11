package dvoraka.avservice.server.runner;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.runner.ServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.checker.DefaultPerformanceTester;
import dvoraka.avservice.server.configuration.jms.JmsConfig;

import java.io.IOException;

/**
 * JMS load test runner.
 */
public class JmsLoadTestRunner extends AbstractServiceRunner {

    public static void main(String[] args) throws IOException {
        ServiceRunner runner = new JmsLoadTestRunner();
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
    protected Class<? extends ServiceManagement> runClass() {
        return DefaultPerformanceTester.class;
    }

    @Override
    protected void waitForKey() throws IOException {
        // no waiting
    }
}
