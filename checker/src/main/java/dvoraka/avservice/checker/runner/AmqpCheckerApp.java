package dvoraka.avservice.checker.runner;

import dvoraka.avservice.checker.AvChecker;
import dvoraka.avservice.checker.configuration.AmqpCheckerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Testing utility for AMQP anti-virus module.
 */
public final class AmqpCheckerApp {

    private static final Logger log = LogManager.getLogger();


    private AmqpCheckerApp() {
    }

    public static void printHeader() {
        System.out.println("AV checker");
        System.out.println("==========");
    }

    public static void main(String[] args) throws java.io.IOException {
        printHeader();

        AbstractApplicationContext context =
                new AnnotationConfigApplicationContext(AmqpCheckerConfig.class);

        // check
        log.debug("Start checking...");
        System.out.println("CHECK:");
        AvChecker avc = context.getBean(AvChecker.class);
        avc.check();
        log.debug("Check completed.");

        context.close();
    }
}
