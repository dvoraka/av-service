package dvoraka.avservice.checker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Testing utility for AMQP anti-virus module.
 */
public final class CheckerApp {

    private static Logger logger = LogManager.getLogger();


    private CheckerApp() {
    }

    public static void printHeader() {
        System.out.println("AV checker");
        System.out.println("==========");
    }

    public static void main(String[] args) throws java.io.IOException {

        printHeader();

        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // protocol negotiation
//        String[] protocols = {"0.1", "1.0"};
//        AVUtils utils = context.getBean(AVUtils.class);
//        System.out.println("Negotiated protocol: "
//                + utils.findProtocol(protocols));

        // check
        logger.debug("Start checking...");
        System.out.println("CHECK:");
        AVChecker avc = context.getBean(AVChecker.class);
        avc.check();
        logger.debug("Check completed.");

        context.close();
    }
}
