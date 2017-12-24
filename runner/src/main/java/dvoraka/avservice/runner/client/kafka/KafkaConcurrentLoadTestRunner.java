package dvoraka.avservice.runner.client.kafka;

import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.client.perf.ConcurrentPerformanceTester;
import dvoraka.avservice.common.runner.AbstractAppRunner;
import dvoraka.avservice.common.runner.AppRunner;
import dvoraka.avservice.common.service.ApplicationManagement;

/**
 * Kafka concurrent load test runner.
 */
public class KafkaConcurrentLoadTestRunner extends AbstractAppRunner {

    public static void main(String[] args) {
        AppRunner runner = new KafkaConcurrentLoadTestRunner();
        runner.run();
    }

    @Override
    protected String[] profiles() {
        return new String[]{"client", "performance", "concurrent", "kafka", "file-client", "no-db"};
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
