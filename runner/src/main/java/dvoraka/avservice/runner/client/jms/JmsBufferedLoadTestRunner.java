package dvoraka.avservice.runner.client.jms;

import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.client.perf.BufferedPerformanceTester;
import dvoraka.avservice.common.runner.AbstractAppRunner;
import dvoraka.avservice.common.runner.AppRunner;
import dvoraka.avservice.common.service.ApplicationManagement;

/**
 * JMS buffered load test runner.
 */
public class JmsBufferedLoadTestRunner extends AbstractAppRunner {

    public static void main(String[] args) {
        AppRunner runner = new JmsBufferedLoadTestRunner();
        runner.run();
    }

    @Override
    protected String[] profiles() {
        return new String[]{"client", "performance", "buffered", "jms", "file-client", "no-db"};
    }

    @Override
    protected Class<?>[] configClasses() {
        return new Class<?>[]{ClientConfig.class};
    }

    @Override
    protected Class<? extends ApplicationManagement> runClass() {
        return BufferedPerformanceTester.class;
    }
}
