package dvoraka.avservice.client.checker;

import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.client.service.response.ResponseClient;
import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.service.ApplicationManagement;
import dvoraka.avservice.common.testing.PerformanceTest;
import dvoraka.avservice.common.testing.PerformanceTestProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.Objects.requireNonNull;


/**
 * Buffered tester for a performance testing.
 */
@Component
public class BufferedPerformanceTester implements PerformanceTest, ApplicationManagement {

    private final AvServiceClient avServiceClient;
    private final ResponseClient responseClient;
    private final PerformanceTestProperties testProperties;

    private static final Logger log = LogManager.getLogger(BufferedPerformanceTester.class);

    private static final float MS_PER_SECOND = 1000f;

    private volatile boolean running;
    private volatile boolean passed;

    private float result;


    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment()
                .setActiveProfiles("client", "amqp", "file-client", "no-db");

        context.register(ClientConfig.class);
        context.refresh();

        PerformanceTest test = new BufferedPerformanceTester(
                context.getBean(AvServiceClient.class),
                context.getBean(ResponseClient.class),
                context.getBean(PerformanceTestProperties.class));

        test.run();

        context.close();
    }

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
        running = true;
        final long loops = testProperties.getMsgCount();
        System.out.println("Load test start for " + loops + " messages...");

        final int bufferSize = 6;
        BlockingQueue<AvMessage> buffer = new ArrayBlockingQueue<>(bufferSize);

        long start = System.currentTimeMillis();
        for (int loop = 0; loop <= loops; loop++) {
            AvMessage message = Utils.genInfectedMessage();
            try {
                buffer.put(message);
            } catch (InterruptedException e) {
                log.warn("Interrupted.", e);
                Thread.currentThread().interrupt();
            }

            avServiceClient.checkMessage(message);

            while (buffer.size() >= bufferSize) {
                getMessages(buffer);
            }
        }

        while (buffer.size() > 0) {
            getMessages(buffer);
        }

        long duration = System.currentTimeMillis() - start;
        System.out.println("Load test end.");

        float durationSeconds = duration / MS_PER_SECOND;
        setResult(loops / durationSeconds);

        System.out.println("\nDuration: " + durationSeconds + " s");
        System.out.println("Messages: " + result + "/s");

        running = false;
    }

    //TODO: add max timeout
    private void getMessages(BlockingQueue<AvMessage> buffer) {
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
