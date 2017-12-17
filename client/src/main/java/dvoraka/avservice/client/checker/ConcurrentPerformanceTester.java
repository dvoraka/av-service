package dvoraka.avservice.client.checker;

import dvoraka.avservice.client.transport.AvNetworkComponent;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.helper.ExecutorServiceHelper;
import dvoraka.avservice.common.testing.AbstractPerformanceTester;
import dvoraka.avservice.common.testing.PerformanceTestProperties;
import dvoraka.avservice.common.util.Utils;
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
public class ConcurrentPerformanceTester extends AbstractPerformanceTester
        implements ExecutorServiceHelper {

    private final AvNetworkComponent avNetworkComponent;
    private final PerformanceTestProperties testProperties;

    private final ConcurrentMap<String, Boolean> messages;
    private final ExecutorService executorService;
    private final AtomicLong counter;


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
        setRunning(true);

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

        setRunning(false);
        setDone(true);
    }

    @PreDestroy
    private void stop() {
        final int maxWaitTime = 5;
        shutdownAndAwaitTermination(executorService, maxWaitTime, log);
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
