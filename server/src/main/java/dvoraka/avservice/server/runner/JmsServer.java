package dvoraka.avservice.server.runner;

import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.configuration.JmsConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * JMS server runner.
 */
@Deprecated
public final class JmsServer {

    private static boolean testRun;


    private JmsServer() {
    }

    public static void setTestRun(boolean testRun) {
        JmsServer.testRun = testRun;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("jms", "jms-async", "database");
        context.register(JmsConfig.class);
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
