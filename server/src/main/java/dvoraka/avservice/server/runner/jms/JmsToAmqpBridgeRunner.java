package dvoraka.avservice.server.runner.jms;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.runner.ServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.ServerComponentBridge;
import dvoraka.avservice.server.configuration.bridge.JmsToAmqpConfig;

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
        return new String[]{"core", "jms2amqp", "db"};
    }

    @Override
    public Class<?>[] configClasses() {
        return new Class<?>[]{JmsToAmqpConfig.class};
    }

    @Override
    public Class<? extends ServiceManagement> runClass() {
        return ServerComponentBridge.class;
    }
}
