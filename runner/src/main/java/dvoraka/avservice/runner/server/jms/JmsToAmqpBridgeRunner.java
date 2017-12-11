package dvoraka.avservice.runner.server.jms;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.runner.ServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.AvNetworkComponentBridge;
import dvoraka.avservice.server.configuration.BridgeConfig;

import java.io.IOException;

/**
 * JMS to AMQP bridge runner.
 */
public class JmsToAmqpBridgeRunner extends AbstractServiceRunner {

    public static void main(String[] args) throws IOException {
        ServiceRunner runner = new JmsToAmqpBridgeRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{"bridge", "jms", "to-amqp", "db"};
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
