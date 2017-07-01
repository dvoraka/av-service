package dvoraka.avservice.server.runner;

import dvoraka.avservice.client.checker.BufferedPerformanceTester;
import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.common.runner.AbstractAppRunner;
import dvoraka.avservice.common.runner.AppRunner;
import dvoraka.avservice.common.service.ApplicationManagement;

import java.io.IOException;

/**
 * Kafka buffered load test runner.
 */
public class KafkaBufferedLoadTestRunner extends AbstractAppRunner {

    public static void main(String[] args) throws IOException {
        AppRunner runner = new KafkaBufferedLoadTestRunner();
        runner.run();
    }

    @Override
    protected String[] profiles() {
        return new String[]{"client", "performance", "kafka", "file-client", "no-db"};
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
