package dvoraka.avservice.runner.server.amqp;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.runner.ServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.AvNetworkComponentBridge;
import dvoraka.avservice.server.configuration.BridgeConfig;

/**
 * AMQP to JMS bridge runner.
 */
public class AmqpToJmsBridgeRunner extends AbstractServiceRunner {

    public static void main(String[] args) {
        ServiceRunner runner = new AmqpToJmsBridgeRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{"bridge", "amqp", "to-jms", "db"};
    }

    @Override
    public Class<?>[] configClasses() {
        return new Class<?>[]{BridgeConfig.class};
    }

    @Override
    public Class<? extends ServiceManagement> runClass() {
        return AvNetworkComponentBridge.class;
    }
}
