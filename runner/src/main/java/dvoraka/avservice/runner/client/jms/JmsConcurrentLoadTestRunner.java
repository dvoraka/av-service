package dvoraka.avservice.runner.client.jms;

import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.client.perf.ConcurrentPerformanceTester;
import dvoraka.avservice.common.runner.AbstractAppRunner;
import dvoraka.avservice.common.runner.AppRunner;
import dvoraka.avservice.common.service.ApplicationManagement;

/**
 * JMS concurrent load test runner.
 */
public class JmsConcurrentLoadTestRunner extends AbstractAppRunner {

    public static void main(String[] args) {
        AppRunner runner = new JmsConcurrentLoadTestRunner();
        runner.run();
    }

    @Override
    protected String[] profiles() {
        return new String[]{"client", "performance", "concurrent", "jms", "file-client", "no-db"};
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
