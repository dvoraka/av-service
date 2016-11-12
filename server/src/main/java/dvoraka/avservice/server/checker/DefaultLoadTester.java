package dvoraka.avservice.server.checker;

import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MessageNotFoundException;
import dvoraka.avservice.common.service.ServiceManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class for load testing.
 */
@Component
public class DefaultLoadTester implements ServiceManagement {

    private final Checker checker;


    @Autowired
    public DefaultLoadTester(Checker checker) {
        this.checker = checker;
    }

    @Override
    public void start() {
        final int loops = 10_000;
        AvMessage message;
        for (int i = 0; i < loops; i++) {
            message = Utils.genInfectedMessage();

            checker.sendMessage(message);
            try {
                checker.receiveMessage(message.getId());
            } catch (MessageNotFoundException e) {
                e.printStackTrace();
            }
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
