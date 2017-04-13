package dvoraka.avservice.server.runner.amqp;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.runner.ServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.configuration.ServerConfig;

import java.io.IOException;

/**
 * AMQP server runner.
 */
public class AmqpServerRunner extends AbstractServiceRunner {

    public static void main(String[] args) throws IOException {
        ServiceRunner runner = new AmqpServerRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{"core", "server", "client", "amqp", "amqp-server", "db"};
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
