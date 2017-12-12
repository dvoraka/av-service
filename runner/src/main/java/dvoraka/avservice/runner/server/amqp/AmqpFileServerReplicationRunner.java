package dvoraka.avservice.runner.server.amqp;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.runner.ServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.configuration.ServerConfig;

/**
 * Amqp file server with replication runner.
 */
public class AmqpFileServerReplicationRunner extends AbstractServiceRunner {

    public static void main(String[] args) {
        ServiceRunner runner = new AmqpFileServerReplicationRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{
                "core", "server", "replication", "client", "amqp", "storage", "db"
        };
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
