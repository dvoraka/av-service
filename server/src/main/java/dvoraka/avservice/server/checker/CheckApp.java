package dvoraka.avservice.server.checker;

import dvoraka.avservice.common.service.ApplicationManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * App for checking.
 */
@Component
public class CheckApp implements ApplicationManagement {

    private static final Logger log = LogManager.getLogger(CheckApp.class);

    private final Checker checker;

    private volatile boolean running;


    @Autowired
    public CheckApp(Checker checker) {
        this.checker = checker;
    }

    @Override
    public void start() {
        setRunning(true);
        log.info("Starting check...");

        System.out.print("Checking... ");

        if (checker.check()) {
            System.out.println("OK");
        } else {
            System.out.println("failed!");
        }

        log.info("Check completed.");
        setRunning(false);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}