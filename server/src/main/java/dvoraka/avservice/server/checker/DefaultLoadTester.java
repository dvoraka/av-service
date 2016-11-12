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

    private static final float MS_PER_SECOND = 1000f;

    private final Checker checker;


    @Autowired
    public DefaultLoadTester(Checker checker) {
        this.checker = checker;
    }

    @Override
    public void start() {
        final int loops = 10_000;
        System.out.println("Load test start for " + loops + " messages...");

        long start = System.currentTimeMillis();
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

        long duration = System.currentTimeMillis() - start;
        System.out.println("Load test end.");

        float durationSeconds = duration / MS_PER_SECOND;
        System.out.println("\nDuration: " + durationSeconds + " s");
        System.out.println("Messages: " + loops / durationSeconds + "/s");
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
