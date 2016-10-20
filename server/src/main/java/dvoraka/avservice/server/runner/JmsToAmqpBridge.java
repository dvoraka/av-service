package dvoraka.avservice.server.runner;

import dvoraka.avservice.server.ServerComponentBridge;
import dvoraka.avservice.server.configuration.JmsToAmqpConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * JMS to AMQP bridge.
 */
public final class JmsToAmqpBridge {

    private static boolean testRun;


    private JmsToAmqpBridge() {
    }

    public static void setTestRun(boolean testRun) {
        JmsToAmqpBridge.testRun = testRun;
    }

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("db-solr", "jms2amqp");
        context.register(JmsToAmqpConfig.class);
        context.refresh();

        ServerComponentBridge bridge = context.getBean(ServerComponentBridge.class);
        bridge.start();

        if (!testRun) {
            System.out.println("Press Enter to stop the bridge.");
            System.in.read();
        }

        context.close();
    }
}
