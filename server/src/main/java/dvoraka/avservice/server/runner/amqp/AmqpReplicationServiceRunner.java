package dvoraka.avservice.server.runner.amqp;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.runner.ServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.storage.configuration.StorageConfig;
import dvoraka.avservice.storage.replication.ReplicationServiceApp;

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
        return new String[]{"storage", "replication", "client", "amqp", "amqp-client", "no-db"};
    }

    @Override
    protected Class<?>[] configClasses() {
        return new Class<?>[]{StorageConfig.class};
    }

    @Override
    protected Class<? extends ServiceManagement> runClass() {
        return ReplicationServiceApp.class;
    }
}
