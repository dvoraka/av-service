package dvoraka.avservice.client.checker;

import dvoraka.avservice.client.transport.AvNetworkComponent;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.service.ApplicationManagement;
import dvoraka.avservice.common.testing.PerformanceTest;
import dvoraka.avservice.common.testing.PerformanceTestProperties;
import dvoraka.avservice.common.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.requireNonNull;

/**
 * Concurrent performance tester.
 */
//TODO: complete
@Component
public class ConcurrentPerformanceTester implements PerformanceTest, ApplicationManagement {

    private final AvNetworkComponent avNetworkComponent;
    private final PerformanceTestProperties testProperties;

    private static final Logger log = LogManager.getLogger(ConcurrentPerformanceTester.class);

    private final ConcurrentMap<String, Boolean> messages;
    private final ExecutorService executorService;
    private final AtomicLong counter;

    private volatile boolean running;


    @Autowired
    public ConcurrentPerformanceTester(
            AvNetworkComponent avNetworkComponent,
            PerformanceTestProperties testProperties
    ) {
        this.avNetworkComponent = requireNonNull(avNetworkComponent);
        this.testProperties = requireNonNull(testProperties);

        messages = new ConcurrentHashMap<>();
        executorService = Executors.newFixedThreadPool(testProperties.getThreadCount());
        counter = new AtomicLong();
    }

    @Override
    public void start() {
        running = true;

        avNetworkComponent.addMessageListener(this::onMessage);

        final long messageCount = testProperties.getMsgCount();
        log.info("Load test start for " + messageCount + " messages...");

        long start = System.currentTimeMillis();
        for (int i = 0; i < messageCount; i++) {
            executorService.execute(this::sendTestingMessage);
        }

        while (counter.get() != messageCount) {
            try {
                log.debug("Waiting...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.warn("Test interrupted!", e);
                Thread.currentThread().interrupt();
                return;
            }
        }

        long duration = System.currentTimeMillis() - start;
        log.info("Load test end.");

        float durationSeconds = duration / 1_000.0f;
//        setResult(loops / durationSeconds);

        log.info("Duration: " + durationSeconds + " s");
        log.info("Messages: " + (messageCount / durationSeconds) + "/s");

        running = false;
    }

    @PreDestroy
    private void stop() {
        // stop the executor service
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public long getResult() {
        return 0;
    }

    @Override
    public void run() {
        start();
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean passed() {
        return false;
    }

    private void sendTestingMessage() {
        //TODO: use normal and infected messages
        AvMessage message = Utils.genInfectedMessage();
        avNetworkComponent.sendMessage(message);

        messages.put(message.getId(), true);
    }

    private void onMessage(AvMessage message) {

        if (messages.containsKey(message.getCorrelationId())) {
            if (Utils.OK_VIRUS_INFO.equals(message.getVirusInfo())
                    && !messages.get(message.getCorrelationId())) {

                counter.getAndIncrement();

            } else if (!Utils.OK_VIRUS_INFO.equals(message.getVirusInfo())
                    && messages.get(message.getCorrelationId())) {

                counter.getAndIncrement();
            }
        }
    }
}
