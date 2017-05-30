package dvoraka.avservice.server.runner.amqp;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.runner.ServiceRunner;
import dvoraka.avservice.common.service.BasicServiceManagement;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.storage.configuration.StorageConfig;

/**
 * AMQP replication service runner.
 */
public class AmqpReplicationServiceRunner extends AbstractServiceRunner {

    public static void main(String[] args) {
        ServiceRunner runner = new AmqpReplicationServiceRunner();
        runner.run();
    }

    @Override
    protected String[] profiles() {
        return new String[]{"storage", "replication", "client", "amqp", "db-mem"};
    }

    @Override
    protected Class<?>[] configClasses() {
        return new Class<?>[]{StorageConfig.class};
    }

    @Override
    protected Class<? extends ServiceManagement> runClass() {
        return BasicServiceManagement.class;
    }
}
