package dvoraka.avservice.checker;

import dvoraka.avservice.checker.configuration.CheckerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Testing utility for AMQP anti-virus module.
 */
public final class CheckerApp {

    private static final Logger log = LogManager.getLogger();


    private CheckerApp() {
    }

    public static void printHeader() {
        System.out.println("AV checker");
        System.out.println("==========");
    }

    public static void main(String[] args) throws java.io.IOException {
        printHeader();

        AbstractApplicationContext context =
                new AnnotationConfigApplicationContext(CheckerConfig.class);

        // protocol negotiation
//        String[] protocols = {"0.1", "1.0"};
//        AvUtils utils = context.getBean(AvUtils.class);
//        System.out.println("Negotiated protocol: "
//                + utils.findProtocol(protocols));

        // check
        log.debug("Start checking...");
        System.out.println("CHECK:");
        AvChecker avc = context.getBean(AvChecker.class);
        avc.check();
        log.debug("Check completed.");

        context.close();
    }
}
