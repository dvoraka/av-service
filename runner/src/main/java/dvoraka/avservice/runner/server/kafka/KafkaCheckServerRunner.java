package dvoraka.avservice.runner.server.kafka;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.runner.ServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.configuration.ServerConfig;

/**
 * Kafka AV check server runner.
 */
public class KafkaCheckServerRunner extends AbstractServiceRunner {

    public static void main(String[] args) {
        ServiceRunner runner = new KafkaCheckServerRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{"core", "check", "server", "kafka", "db"};
    }

    @Override
    public Class<?>[] configClasses() {
        return new Class<?>[]{ServerConfig.class};
    }

    @Override
    public Class<? extends ServiceManagement> runClass() {
        return BasicAvServer.class;
    }
}
