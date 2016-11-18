package dvoraka.avservice.server.checker;

import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MessageNotFoundException;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.common.testing.LoadTestProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Class for load testing.
 */
@Component
public class DefaultLoadTester implements ServiceManagement {

    private static final Logger log = LogManager.getLogger(DefaultLoadTester.class.getName());
    private static final float MS_PER_SECOND = 1000f;

    private final Checker checker;
    private final LoadTestProperties testProperties;


    @Autowired
    public DefaultLoadTester(Checker checker, LoadTestProperties testProperties) {
        this.checker = checker;
        this.testProperties = testProperties;
    }

    @Override
    public void start() {
        int loops = testProperties.getMsgCount();
        System.out.println("Load test start for " + loops + " messages...");

        long start = System.currentTimeMillis();
        AvMessage message;
        for (int i = 0; i < loops; i++) {
            message = Utils.genInfectedMessage();
            checker.sendMessage(message);
            try {
                checker.receiveMessage(message.getId());
            } catch (MessageNotFoundException e) {
                log.warn("Message not found.", e);
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
