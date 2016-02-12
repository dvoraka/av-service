package dvoraka.avservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Main AV message processor.
 */
public class DefaultMessageProcessor implements MessageProcessor {

    @Autowired
    private AVService avService;

    private static final Logger log = LogManager.getLogger(SimpleAmqpListeningStrategy.class.getName());

    private Queue<AVMessage> processedMessages = new LinkedBlockingQueue<>(10);
    private ExecutorService executorService;

    private boolean running;


    public DefaultMessageProcessor() {
        executorService = Executors.newFixedThreadPool(10);
    }

    @Override

    public void sendMessage(AVMessage message) {

        setRunning(true);

        log.debug("Processing message...");
        Runnable process = new Runnable() {
            @Override
            public void run() {
                processMessage(message);
            }
        };

        executorService.execute(process);
        log.debug("Message processed.");
    }

    private void processMessage(AVMessage message) {

        log.debug("Waiting queue size: " + processedMessages.size());

        log.debug("Scanning thread: " + Thread.currentThread().getName());
        avService.scanStream(message.getData());

        while (isRunning()) {
            try {
                processedMessages.add(message);
                break;
            } catch (IllegalStateException e) {
                log.warn("Processed queue for thread "
                        + Thread.currentThread().getName() + " is full");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean hasProcessedMessage() {
        return !processedMessages.isEmpty();
    }

    @Override
    public AVMessage getProcessedMessage() {
        return processedMessages.poll();
    }

    @Override
    public void stop() {

        setRunning(false);

        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
