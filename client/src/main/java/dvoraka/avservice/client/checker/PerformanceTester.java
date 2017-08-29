package dvoraka.avservice.client.checker;

import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MessageNotFoundException;
import dvoraka.avservice.common.service.ApplicationManagement;
import dvoraka.avservice.common.testing.PerformanceTest;
import dvoraka.avservice.common.testing.PerformanceTestProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;


/**
 * Class for performance testing.
 */
@Component
public class PerformanceTester implements PerformanceTest, ApplicationManagement {

    private final Checker checker;
    private final PerformanceTestProperties testProperties;

    private static final Logger log = LogManager.getLogger(PerformanceTester.class);

    private static final float MS_PER_SECOND = 1000f;

    private volatile boolean running;
    private volatile boolean passed;

    private float result;


    @Autowired
    public PerformanceTester(Checker checker, PerformanceTestProperties testProperties) {
        this.checker = requireNonNull(checker);
        this.testProperties = requireNonNull(testProperties);
    }

    @Override
    public void start() {
        running = true;

        boolean perfect = true;
        final long loops = testProperties.getMsgCount();
        final long maxRate = testProperties.getMaxRate();
        log.info("Load test start for " + loops + " messages...");

        long start = System.currentTimeMillis();
        long maxRateCounter = start;
        for (int i = 0; i < loops; i++) {
            AvMessage message = Utils.genInfectedMessage();
            checker.sendAvMessage(message);
            try {
                checker.receiveMessage(message.getId());
            } catch (MessageNotFoundException e) {
                log.warn("Message not found.", e);
                perfect = false;
            }

            if (maxRate == 0) {
                continue;
            }

            long delta = System.currentTimeMillis() - maxRateCounter;
            if (i % maxRate == 0 && delta < MS_PER_SECOND) {
                sleep(delta);
                maxRateCounter = System.currentTimeMillis();
            }
        }

        long duration = System.currentTimeMillis() - start;
        log.info("Load test end.");

        float durationSeconds = duration / MS_PER_SECOND;
        setResult(loops / durationSeconds);

        log.info("\nDuration: " + durationSeconds + " s");
        log.info("Messages: " + result + "/s");

        if (!perfect) {
            log.warn("\nSome messages were lost.");
        } else {
            passed = true;
        }

        running = false;
    }

    /**
     * Sleeps for (1000 - duration) ms.
     *
     * @param duration the duration
     */
    private void sleep(long duration) {
        final float correction = 0.98f;
        long sleepTime = (long) ((MS_PER_SECOND * correction) - duration);

        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                log.warn("Sleeping interrupted!", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        start();
    }

    @Override
    public boolean isDone() {
        return !running;
    }

    @Override
    public boolean passed() {
        return passed;
    }

    private void setResult(float result) {
        this.result = result;
    }

    /**
     * Returns messages per second.
     *
     * @return messages/second
     */
    @Override
    public long getResult() {
        return (long) result;
    }
}
