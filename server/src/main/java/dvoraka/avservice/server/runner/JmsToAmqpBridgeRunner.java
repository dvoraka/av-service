package dvoraka.avservice.server.runner;

import dvoraka.avservice.common.runner.AbstractRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.ServerComponentBridge;
import dvoraka.avservice.server.configuration.jms.JmsToAmqpConfig;

import java.io.IOException;

/**
 * JMS to AMQP bridge runner.
 */
public class JmsToAmqpBridgeRunner extends AbstractRunner {

    public static void main(String[] args) throws IOException {
        JmsToAmqpBridgeRunner runner = new JmsToAmqpBridgeRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{"core", "jms2amqp", "db-solr"};
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
