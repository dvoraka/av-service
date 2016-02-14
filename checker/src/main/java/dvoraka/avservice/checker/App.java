package dvoraka.avservice.checker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Testing utility for anti-virus module.
 */
public class App {

    private static Logger logger = LogManager.getLogger();

    public static void printHeader() {
        System.out.println("AV checker");
        System.out.println("==========");
    }

    public static void main(String[] args) throws java.io.IOException {

        printHeader();

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // protocol negotiation
//        String[] protocols = {"0.1", "1.0"};
//        AVUtils utils = context.getBean(AVUtils.class);
//        System.out.println("Negotiated protocol: "
//                + utils.findProtocol(protocols));

        // check
        System.out.println("CHECK:");
        AVChecker avc = context.getBean(AVChecker.class);
        avc.check();
    }
}
