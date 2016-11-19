package dvoraka.avservice.server.checker;

import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MessageNotFoundException;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.common.testing.PerformanceTestProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;


/**
 * Common class performance testing.
 */
@Component
public class DefaultPerformanceTester implements ServiceManagement {

    private static final Logger log =
            LogManager.getLogger(DefaultPerformanceTester.class.getName());
    private static final float MS_PER_SECOND = 1000f;

    private final Checker checker;
    private final PerformanceTestProperties testProperties;

    private boolean running;
    private boolean started;
    private boolean stopped;


    @Autowired
    public DefaultPerformanceTester(Checker checker, PerformanceTestProperties testProperties) {
        Objects.requireNonNull(testProperties, "Test properties must not be null!");

        this.checker = checker;
        this.testProperties = testProperties;
    }

    @Override
    public void start() {
        stopped = false;
        started = true;
        running = true;

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
        stopped = true;
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }
}
