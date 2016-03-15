package dvoraka.avservice;

import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.MessageStatus;
import dvoraka.avservice.server.ReceivingType;
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

    private static final int DEFAULT_QUEUE_SIZE = 100;
    private static final long POOL_TERM_TIME_S = 20;

    private Map<String, Long> processingMessages;
    private Map<String, Long> processedMessages;

    private Queue<AVMessage> processedMessagesQueue;
    private List<AVMessageListener> observers = new ArrayList<>();
    private ExecutorService executorService;
    private ReceivingType serverReceivingType = ReceivingType.POLLING;

    private int threadCount;
    private boolean running;
    private int queueSize;


    public DefaultMessageProcessor(int threadCount) {
        this(threadCount, DEFAULT_QUEUE_SIZE);
    }

    public DefaultMessageProcessor(int threadCount, int queueSize) {
        this.threadCount = threadCount;
        this.queueSize = queueSize;

        ThreadFactory threadFactory = new CustomThreadFactory("message-processor-");
        executorService = Executors.newFixedThreadPool(threadCount, threadFactory);

        processingMessages = new ConcurrentHashMap<>(queueSize);
        processedMessages = new ConcurrentHashMap<>(queueSize);
        processedMessagesQueue = new LinkedBlockingQueue<>(queueSize);
    }

    @Override
    public void sendMessage(AVMessage message) {

        setRunning(true);

        log.debug("Processing message...");
        addProcessingMessage(message.getId());

        Runnable process = () -> processMessage(message);
        executorService.execute(process);
        log.debug("Message accepted.");
    }

    @Override
    public MessageStatus messageStatus(String id) {
        log.debug("Message status call from: " + Thread.currentThread().getName());

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
        log.debug("Scanning done in: " + Thread.currentThread().getName());

        // TODO: Delete after some time?
        addProcessedMessage(message.getId());
        removeProcessingMessage(message.getId());

        sendResponse(prepareResponse(message, infected));
    }

    private AVMessage prepareResponse(AVMessage message, boolean infected) {

        return message.createResponse(infected);
    }

    private void sendResponse(AVMessage message) {
        if (getServerReceivingType() == ReceivingType.LISTENER) {
            notifyObservers(message);
        } else if (getServerReceivingType() == ReceivingType.POLLING) {
            saveMessage(message);
        }
    }

    private void saveMessage(AVMessage message) {
        while (isRunning()) {
            try {
                // add message to the queue
                processedMessagesQueue.add(message);
                break;
            } catch (IllegalStateException e) {
                // full queue
                log.warn("Processed queue for the thread " + Thread.currentThread().getName() + " is full");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    log.warn("Waiting interrupted!", e);
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
            executorService.awaitTermination(POOL_TERM_TIME_S, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Stopping the thread pool failed!", e);
        }
    }

    @Override
    public void addAVMessageListener(AVMessageListener listener) {
        setServerReceivingType(ReceivingType.LISTENER);
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

    private void addProcessingMessage(String id) {
        processingMessages.put(id, System.currentTimeMillis());
    }

    private void addProcessedMessage(String id) {
        processedMessages.put(id, System.currentTimeMillis());
    }

    private void removeProcessingMessage(String id) {
        processingMessages.remove(id);
    }

    private void removeProcessedMessage(String id) {
        processedMessages.remove(id);
    }

    public void setAvService(AVService avService) {
        this.avService = avService;
    }

    private void setServerReceivingType(ReceivingType type) {
        serverReceivingType = type;
    }

    public ReceivingType getServerReceivingType() {
        return serverReceivingType;
    }

    public int getQueueSize() {
        return queueSize;
    }
}
