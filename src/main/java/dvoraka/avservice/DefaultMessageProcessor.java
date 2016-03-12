package dvoraka.avservice;

import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.MessageStatus;
import dvoraka.avservice.server.SimpleAmqpListeningStrategy;
import dvoraka.avservice.service.AVService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Main AV message processor.
 */
public class DefaultMessageProcessor implements MessageProcessor {

    @Autowired
    private AVService avService;

    private static final Logger log = LogManager.getLogger(SimpleAmqpListeningStrategy.class.getName());

    private static final int QUEUE_SIZE = 100;

    private Map<String, Long> processingMessages = new ConcurrentHashMap<>(QUEUE_SIZE);
    private Map<String, Long> processedMessages = new ConcurrentHashMap<>(QUEUE_SIZE);

    private Queue<AVMessage> processedMessagesQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
    private List<AVMessageListener> observers = new ArrayList<>();
    private ExecutorService executorService;
    private int threadCount;
    private boolean running;


    public DefaultMessageProcessor(int threadCount) {
        this.threadCount = threadCount;
        ThreadFactory threadFactory = new CustomThreadFactory("message-processor-");
        executorService = Executors.newFixedThreadPool(threadCount, threadFactory);
    }

    @Override
    public void sendMessage(AVMessage message) {

        setRunning(true);

        log.debug("Processing message...");
        processingMessages.put(message.getId(), System.currentTimeMillis());

        Runnable process = () -> processMessage(message);
        executorService.execute(process);
        log.debug("Message sent.");
    }

    @Override
    public MessageStatus messageStatus(String id) {
        if (processedMessages.containsKey(id)) {
            return MessageStatus.PROCESSED;
        } else if (processingMessages.containsKey(id)) {
            return MessageStatus.PROCESSING;
        } else {
            return MessageStatus.UNKNOWN;
        }
    }

    private void processMessage(AVMessage message) {

        log.debug("Waiting queue size: " + processedMessagesQueue.size());

        log.debug("Scanning thread: " + Thread.currentThread().getName());
        boolean infected = avService.scanStream(message.getData());

        while (isRunning()) {
            try {
                AVMessage avMessage = message.createResponse(infected);
                if (observers.size() == 0) {
                    processedMessagesQueue.add(avMessage);

                    // TODO: Delete after some time?
                    processedMessages.put(message.getId(), System.currentTimeMillis());
                    processingMessages.remove(message.getId());
                } else {
                    notifyObservers(avMessage);
                }
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
        return !processedMessagesQueue.isEmpty();
    }

    @Override
    public AVMessage getProcessedMessage() {
        return processedMessagesQueue.poll();
    }

    @Override
    public void stop() {
        setRunning(false);

        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Stopping thread pool failed!", e);
        }
    }

    @Override
    public void addAVMessageListener(AVMessageListener listener) {
        observers.add(listener);
    }

    private void notifyObservers(AVMessage avMessage) {
        for (AVMessageListener listener : observers) {
            listener.onAVMessage(avMessage);
        }
    }

    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    public int getThreadCount() {
        return threadCount;
    }
}
