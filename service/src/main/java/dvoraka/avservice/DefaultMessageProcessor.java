package dvoraka.avservice;

import dvoraka.avservice.common.CustomThreadFactory;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.common.exception.ScanErrorException;
import dvoraka.avservice.common.ReceivingType;
import dvoraka.avservice.service.AvService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.annotation.PreDestroy;
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
import java.util.concurrent.atomic.AtomicLong;

/**
 * Main AV message processor.
 */
@ManagedResource
public class DefaultMessageProcessor implements MessageProcessor {

    @Autowired
    private AvService avService;

    private static final Logger log = LogManager.getLogger(DefaultMessageProcessor.class.getName());

    public static final int DEFAULT_QUEUE_SIZE = 100;
    public static final ReceivingType DEFAULT_RECEIVING_TYPE = ReceivingType.POLLING;
    private static final long POOL_TERM_TIME_S = 20;

    private Map<String, Long> processingMessages;
    private Map<String, Long> processedMessages;
    private AtomicLong receivedMsgCount = new AtomicLong();
    private AtomicLong processedMsgCount = new AtomicLong();

    private Queue<AvMessage> processedMessagesQueue;
    private List<ProcessedAvMessageListener> observers = new ArrayList<>();
    private ExecutorService executorService;
    private ReceivingType serverReceivingType;

    private int threadCount;
    private boolean running;
    private int queueSize;


    public DefaultMessageProcessor(int threadCount) {
        this(threadCount, DEFAULT_RECEIVING_TYPE, DEFAULT_QUEUE_SIZE);
    }

    public DefaultMessageProcessor(int threadCount, ReceivingType serverReceivingType, int queueSize) {

        this.threadCount = threadCount;
        this.queueSize = queueSize;

        ThreadFactory threadFactory = new CustomThreadFactory("message-processor-");
        executorService = Executors.newFixedThreadPool(threadCount, threadFactory);

        processingMessages = new ConcurrentHashMap<>(queueSize);
        processedMessages = new ConcurrentHashMap<>(queueSize);

        this.serverReceivingType = serverReceivingType;
        if (serverReceivingType == ReceivingType.POLLING) {
            processedMessagesQueue = new LinkedBlockingQueue<>(queueSize);
        }
    }

    @Override
    public void sendMessage(AvMessage message) {
        setRunning(true);
        receivedMsgCount.getAndIncrement();

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

    private void processMessage(AvMessage message) {
        log.debug("Scanning thread: " + Thread.currentThread().getName());

        boolean infected = false;
        String error = null;
        try {
            byte[] data = message.getData();
            if (data != null) {
                infected = avService.scanStream(data);
            } else {
                throw new ScanErrorException("No data in the message.");
            }
        } catch (ScanErrorException e) {
            error = e.getMessage();
            log.warn("Scanning error!", e);
        }
        log.debug("Scanning done in: " + Thread.currentThread().getName());

        // TODO: Delete after some time?
        addProcessedMessage(message.getId());
        removeProcessingMessage(message.getId());

        processedMsgCount.getAndIncrement();

        if (error == null) {
            sendResponse(prepareResponse(message, infected));
        } else {
            sendResponse(prepareErrorResponse(message, error));
        }
    }

    private AvMessage prepareResponse(AvMessage message, boolean infected) {
        return message.createResponse(infected);
    }

    private AvMessage prepareErrorResponse(AvMessage message, String errorMessage) {
        return message.createErrorResponse(errorMessage);
    }

    private void sendResponse(AvMessage message) {
        if (getServerReceivingType() == ReceivingType.LISTENER) {
            notifyObservers(message);
        } else if (getServerReceivingType() == ReceivingType.POLLING) {
            saveMessage(message);
        }
    }

    private void saveMessage(AvMessage message) {
        while (isRunning()) {
            try {
                log.debug("Saving message to the queue...");
                processedMessagesQueue.add(message);
                log.debug("Saved.");
                break;
            } catch (IllegalStateException e) {
                // full queue
                log.warn("Processed queue for the thread " + Thread.currentThread().getName() + " is full");
                final long sleepTime = 500;
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e1) {
                    log.warn("Waiting interrupted!", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public boolean hasProcessedMessage() {
        if (serverReceivingType == ReceivingType.POLLING) {
            return !processedMessagesQueue.isEmpty();
        } else {
            throw new UnsupportedOperationException("Supported for POLLING type only.");
        }
    }

    @Override
    public AvMessage getProcessedMessage() {
        if (serverReceivingType == ReceivingType.POLLING) {
            return processedMessagesQueue.poll();
        } else {
            throw new UnsupportedOperationException("Supported for POLLING type only.");
        }
    }

    public boolean isProcessedQueueFull() {
        return processedMessagesQueue.size() == getQueueSize();
    }

    @Override
    public void stop() {
        log.debug("Stopping thread pool...");
        setRunning(false);

        executorService.shutdown();
        try {
            executorService.awaitTermination(POOL_TERM_TIME_S, TimeUnit.SECONDS);
            log.debug("Stopping done.");
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            log.warn("Stopping the thread pool interrupted!", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void addProcessedAVMessageListener(ProcessedAvMessageListener listener) {
        if (serverReceivingType == ReceivingType.LISTENER) {
            observers.add(listener);
        } else {
            throw new UnsupportedOperationException("Supported for LISTENER type only.");
        }
    }

    @Override
    public void removeProcessedAVMessageListener(ProcessedAvMessageListener listener) {
        observers.remove(listener);
    }

    public int observersCount() {
        return observers.size();
    }

    private void notifyObservers(AvMessage avMessage) {
        for (ProcessedAvMessageListener listener : observers) {
            listener.onProcessedAvMessage(avMessage);
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

    public ReceivingType getServerReceivingType() {
        return serverReceivingType;
    }

    public int getQueueSize() {
        return queueSize;
    }

    @ManagedAttribute
    public long getReceivedMsgCount() {
        return receivedMsgCount.get();
    }

    @ManagedAttribute
    public long getProcessedMsgCount() {
        return processedMsgCount.get();
    }

    @PreDestroy
    public void cleanup() {
        if (isRunning()) {
            stop();
        }
    }
}
