package dvoraka.avservice.server.checker;

import dvoraka.avservice.common.service.ServiceManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * App for checking.
 */
@Component
public class CheckApp implements ServiceManagement {

    private static final Logger log = LogManager.getLogger(CheckApp.class.getName());

    private final Checker checker;


    @Autowired
    public CheckApp(Checker checker) {
        this.checker = checker;
    }

    @Override
    public void start() {
        System.out.print("Checking... ");

        if (checker.check()) {
            System.out.println("OK");
        } else {
            System.out.println("failed!");
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public void restart() {
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean isStopped() {
        return false;
    }
}
