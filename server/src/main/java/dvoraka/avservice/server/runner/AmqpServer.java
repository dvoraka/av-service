package dvoraka.avservice.server.runner;

import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.configuration.AmqpConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * AMQP server runner.
 */
public final class AmqpServer {

    private static boolean testRun = false;


    private AmqpServer() {
    }

    public static void setTestRun(boolean testRun) {
        AmqpServer.testRun = testRun;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("amqp");
        context.register(AmqpConfig.class);
        context.refresh();

        BasicAvServer server = context.getBean(BasicAvServer.class);
        server.start();

        if (!testRun) {
            System.out.println("Press Enter to stop the server.");
            System.in.read();
        }

        context.close();
    }
}
