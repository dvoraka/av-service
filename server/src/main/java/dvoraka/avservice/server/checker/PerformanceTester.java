package dvoraka.avservice.server.checker;

import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MessageNotFoundException;
import dvoraka.avservice.common.service.ApplicationManagement;
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
public class PerformanceTester implements ApplicationManagement {

    private final Checker checker;
    private final PerformanceTestProperties testProperties;

    private static final Logger log = LogManager.getLogger(PerformanceTester.class);

    private static final float MS_PER_SECOND = 1000f;

    private volatile boolean running;


    @Autowired
    public PerformanceTester(Checker checker, PerformanceTestProperties testProperties) {
        this.checker = requireNonNull(checker);
        this.testProperties = requireNonNull(testProperties);
    }

    @Override
    public void start() {
        running = true;

        boolean perfect = true;
        long loops = testProperties.getMsgCount();
        System.out.println("Load test start for " + loops + " messages...");

        long start = System.currentTimeMillis();
        AvMessage message;
        for (int i = 0; i < loops; i++) {
            message = Utils.genInfectedMessage();
            checker.sendAvMessage(message);
            try {
                checker.receiveMessage(message.getId());
            } catch (MessageNotFoundException e) {
                log.warn("Message not found.", e);
                perfect = false;
            }
        }

        long duration = System.currentTimeMillis() - start;
        System.out.println("Load test end.");

        float durationSeconds = duration / MS_PER_SECOND;
        System.out.println("\nDuration: " + durationSeconds + " s");
        System.out.println("Messages: " + loops / durationSeconds + "/s");

        if (!perfect) {
            System.out.println("\nSome messages were lost.");
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
