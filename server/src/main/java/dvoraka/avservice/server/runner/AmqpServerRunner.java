package dvoraka.avservice.server.runner;

import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.configuration.AmqpConfig;

import java.io.IOException;

/**
 * AMQP server runner.
 */
public class AmqpServerRunner extends AbstractRunner {

    public static void main(String[] args) throws IOException {
        AmqpServerRunner runner = new AmqpServerRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{"amqp", "db-solr"};
    }

    @Override
    public Class<?>[] configClasses() {
        return new Class<?>[]{AmqpConfig.class};
    }

    @Override
    public Class<? extends ServiceManagement> runClass() {
        return BasicAvServer.class;
    }
}
