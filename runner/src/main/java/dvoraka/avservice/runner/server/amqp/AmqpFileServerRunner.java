package dvoraka.avservice.runner.server.amqp;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.runner.ServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.configuration.ServerConfig;

/**
 * AMQP file server runner.
 */
public class AmqpFileServerRunner extends AbstractServiceRunner {

    public static void main(String[] args) {
        ServiceRunner runner = new AmqpFileServerRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{"core", "server", "amqp", "storage", "db"};
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
