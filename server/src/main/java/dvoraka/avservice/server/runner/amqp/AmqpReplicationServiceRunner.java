package dvoraka.avservice.server.runner.amqp;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.runner.ServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;

/**
 * AMQP replication service runner.
 */
public class AmqpReplicationServiceRunner extends AbstractServiceRunner {

    public static void main(String[] args) {
        ServiceRunner runner = new AmqpReplicationServiceRunner();
        runner.run();
    }

    @Override
    protected Class<?>[] configClasses() {
        return new Class<?>[0];
    }

    @Override
    protected Class<? extends ServiceManagement> runClass() {
        return null;
    }
}
