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

    private boolean running;


    @Autowired
    public CheckApp(Checker checker) {
        this.checker = checker;
    }

    @Override
    public void start() {
        running = true;
        System.out.print("Checking... ");

        if (checker.check()) {
            System.out.println("OK");
        } else {
            System.out.println("failed!");
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
