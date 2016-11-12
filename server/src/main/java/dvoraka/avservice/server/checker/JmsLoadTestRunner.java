package dvoraka.avservice.server.checker;

import dvoraka.avservice.common.runner.AbstractRunner;
import dvoraka.avservice.common.runner.Runner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.configuration.jms.JmsConfig;

import java.io.IOException;

/**
 * JMS load test runner.
 */
public class JmsLoadTestRunner extends AbstractRunner {

    public static void main(String[] args) throws IOException {
        Runner runner = new JmsLoadTestRunner();
        runner.run();
    }

    @Override
    protected String[] profiles() {
        return new String[]{"core", "jms", "jms-checker", "no-db"};
    }

    @Override
    protected Class<?>[] configClasses() {
        return new Class<?>[]{JmsConfig.class};
    }

    @Override
    protected Class<? extends ServiceManagement> runClass() {
        return DefaultLoadTester.class;
    }

    @Override
    protected void waitForKey() throws IOException {
    }
}
