package dvoraka.avservice.client.perf;

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
@Component
public class ConcurrentPerformanceTester extends AbstractPerformanceTester
        implements ExecutorServiceHelper {

    private final AvNetworkComponent avNetworkComponent;
    private final PerformanceTestProperties testProperties;

    private static final int DEFAULT_TIMEOUT = 3_000;

    private final ConcurrentMap<String, Boolean> sentMessageInfo;
    private final ExecutorService executorService;
    private final AtomicLong counter;

    private int timeout;
    private long lastMsgReceiveTime;


    @Autowired
    public ConcurrentPerformanceTester(
            AvNetworkComponent avNetworkComponent,
            PerformanceTestProperties testProperties
    ) {
        this.avNetworkComponent = requireNonNull(avNetworkComponent);
        this.testProperties = requireNonNull(testProperties);

        sentMessageInfo = new ConcurrentHashMap<>();
        executorService = Executors.newFixedThreadPool(testProperties.getThreadCount());
        counter = new AtomicLong();

        timeout = DEFAULT_TIMEOUT;
    }

    @Override
    public void start() {
        startTest();

        final long msgCount = testProperties.getMsgCount();
        log.info("Load test start for " + msgCount + " messages...");

        avNetworkComponent.addMessageListener(this::onMessage);

        long start = System.currentTimeMillis();
        for (int i = 0; i < msgCount; i++) {
            executorService.execute(this::sendTestingMessage);
        }

        lastMsgReceiveTime = System.currentTimeMillis();
        while (counter.get() != msgCount) {
            try {
                if (System.currentTimeMillis() - lastMsgReceiveTime > getTimeout()) {
                    log.warn("Test timeout!");
                    failTest();

                    return;
                }

                log.debug("Waiting...");
                final int sleepTime = 100;
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                log.warn("Test interrupted!", e);
                Thread.currentThread().interrupt();

                return;
            }
        }

        long duration = System.currentTimeMillis() - start;
        log.info("Load test end.");

        float durationSeconds = duration / MS_PER_SECOND;
        setResult(msgCount / durationSeconds);

        log.info("Duration: " + durationSeconds + " s");
        log.info("Messages: " + (msgCount / durationSeconds) + "/s");

        passTest();
    }

    @PreDestroy
    private void stop() {
        final int maxWaitTimeS = 5;
        shutdownAndAwaitTermination(executorService, maxWaitTimeS, log);
    }

    private void sendTestingMessage() {
        AvMessage message;
        if (System.currentTimeMillis() % 2 == 0) {
            message = Utils.genInfectedMessage();
            sentMessageInfo.put(message.getId(), true);
        } else {
            message = Utils.genMessage();
            sentMessageInfo.put(message.getId(), false);
        }

        avNetworkComponent.sendMessage(message);
    }

    private void onMessage(AvMessage message) {

        if (sentMessageInfo.containsKey(message.getCorrelationId())) {
            if (Utils.OK_VIRUS_INFO.equals(message.getVirusInfo())
                    && !sentMessageInfo.get(message.getCorrelationId())) {

                counter.getAndIncrement();

            } else if (!Utils.OK_VIRUS_INFO.equals(message.getVirusInfo())
                    && sentMessageInfo.get(message.getCorrelationId())) {

                counter.getAndIncrement();
            }

            lastMsgReceiveTime = System.currentTimeMillis();
        }
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
