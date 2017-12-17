package dvoraka.avservice.client.checker;

import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.client.service.response.ResponseClient;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.testing.AbstractPerformanceTester;
import dvoraka.avservice.common.testing.PerformanceTestProperties;
import dvoraka.avservice.common.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;


/**
 * Buffered tester prototype for a performance testing.
 */
@Component
public class BufferedPerformanceTester extends AbstractPerformanceTester {

    private final AvServiceClient avServiceClient;
    private final ResponseClient responseClient;
    private final PerformanceTestProperties testProperties;

    private static final int DEFAULT_TIMEOUT = 1_000;

    private int timeout = DEFAULT_TIMEOUT;


    @Autowired
    public BufferedPerformanceTester(
            AvServiceClient avServiceClient,
            ResponseClient responseClient,
            PerformanceTestProperties testProperties
    ) {
        this.avServiceClient = requireNonNull(avServiceClient);
        this.responseClient = requireNonNull(responseClient);
        this.testProperties = requireNonNull(testProperties);
    }

    @Override
    public void start() {
        setRunning(true);

        final long msgCount = testProperties.getMsgCount();
        log.info("Load test start for " + msgCount + " messages...");

        final int bufferSize = 6;
        final BlockingQueue<AvMessage> buffer = new ArrayBlockingQueue<>(bufferSize);

        long start = System.currentTimeMillis();
        for (int loop = 0; loop < msgCount; loop++) {
            AvMessage message = Utils.genInfectedMessage();
            try {
                buffer.offer(message, getTimeout(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.warn("Interrupted.", e);
                Thread.currentThread().interrupt();
            }

            avServiceClient.checkMessage(message);

            long start2 = System.currentTimeMillis();
            while (buffer.size() >= bufferSize
                    && (System.currentTimeMillis() - start2) < getTimeout()) {
                getMessage(buffer);
            }
        }

        long start3 = System.currentTimeMillis();
        while ((!buffer.isEmpty()) && (System.currentTimeMillis() - start3) < getTimeout()) {
            getMessage(buffer);
        }

        long duration = System.currentTimeMillis() - start;
        log.info("Load test end.");

        float durationSeconds = duration / MS_PER_SECOND;
        setResult(msgCount / durationSeconds);

        log.info("Duration: " + durationSeconds + " s");
        log.info("Messages: " + getResult() + "/s");

        setRunning(false);
        setDone(true);
        setPassed(true);
    }

    private void getMessage(BlockingQueue<AvMessage> buffer) {
        try {
            AvMessage message = buffer.take();
            if (responseClient.getResponse(message.getId()) == null) {
                buffer.put(message);
            }
        } catch (InterruptedException e) {
            log.warn("Interrupted.", e);
            Thread.currentThread().interrupt();
        }
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
