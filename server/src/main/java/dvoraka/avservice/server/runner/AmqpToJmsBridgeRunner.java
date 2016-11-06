package dvoraka.avservice.server.runner;

import dvoraka.avservice.common.runner.AbstractRunner;
import dvoraka.avservice.common.runner.Runner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.ServerComponentBridge;
import dvoraka.avservice.server.configuration.bridge.AmqpToJmsConfig;

import java.io.IOException;

/**
 * AMQP to JMS bridge runner.
 */
public class AmqpToJmsBridgeRunner extends AbstractRunner {

    public static void main(String[] args) throws IOException {
        Runner runner = new AmqpToJmsBridgeRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{"core", "amqp2jms", "db"};
    }

    @Override
    public Class<?>[] configClasses() {
        return new Class<?>[]{AmqpToJmsConfig.class};
    }

    @Override
    public Class<? extends ServiceManagement> runClass() {
        return ServerComponentBridge.class;
    }
}
