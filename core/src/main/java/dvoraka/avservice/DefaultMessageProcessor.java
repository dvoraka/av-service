package dvoraka.avservice;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.CustomThreadFactory;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.common.exception.ScanErrorException;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.service.AvService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Main AV message processor.
 */
@Component
@ManagedResource
public class DefaultMessageProcessor implements MessageProcessor {

    private final AvService avService;
    private final MessageInfoService messageInfoService;

    private static final Logger log = LogManager.getLogger(DefaultMessageProcessor.class);

    private static final int DEFAULT_CACHE_SIZE = 100;
    public static final int DEFAULT_CACHE_TIMEOUT = 100 * 1_000;
    private static final long POOL_TERM_TIME_S = 20;
    private static final AvMessageSource MESSAGE_SOURCE = AvMessageSource.PROCESSOR;

    private ConcurrentMap<String, Long> processingMessages;
    private ConcurrentMap<String, Long> processedMessages;
    private AtomicLong receivedMsgCount = new AtomicLong();
    private AtomicLong processedMsgCount = new AtomicLong();
    /**
     * In milliseconds
     */
    private long processedMsgTimeout = DEFAULT_CACHE_TIMEOUT;

    private List<AvMessageListener> avMessageListeners;
    private ExecutorService executorService;

    private int threadCount;
    private volatile boolean running;
    private String serviceId;


    /**
     * Creates a processor with a given thread count and service ID.
     *
     * @param threadCount the processing thread count
     * @param serviceId   the service ID string
     */
    @Autowired
    public DefaultMessageProcessor(
            int threadCount,
            String serviceId,
            AvService avService,
            MessageInfoService messageInfoService
    ) {
        this.threadCount = threadCount;
        this.serviceId = serviceId;
        this.avService = avService;
        this.messageInfoService = messageInfoService;

        ThreadFactory threadFactory = new CustomThreadFactory("message-processor-");
        executorService = Executors.newFixedThreadPool(threadCount, threadFactory);

        processingMessages = new ConcurrentHashMap<>(DEFAULT_CACHE_SIZE);
        processedMessages = new ConcurrentHashMap<>(DEFAULT_CACHE_SIZE);

        avMessageListeners = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Init and starts processor.
     */
    @PostConstruct
    public void init() {
        start();
    }

    /**
     * Closes and stops all opened resources.
     */
    @PreDestroy
    public void cleanup() {
        if (isRunning()) {
            stop();
        }
    }

    @Override
    public void start() {
        if (!isRunning()) {
            setRunning(true);
            executorService.execute(this::cacheUpdating);
            log.debug("Cache updating started.");
        }
    }

    @Override
    public void sendMessage(AvMessage message) {
        receivedMsgCount.getAndIncrement();

        log.debug("Processing message...");
        addProcessingMessage(message.getId());

        messageInfoService.save(message, MESSAGE_SOURCE, serviceId);

        executorService.execute(() -> processMessage(message));
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

    private void cacheUpdating() {
        HashSet<String> toRemove = new HashSet<>();
        while (isRunning()) {
            long now = System.currentTimeMillis();

            processedMessages.entrySet()
                    .stream()
                    .filter(entry -> (entry.getValue() + processedMsgTimeout) < now)
                    .forEach(entry -> {
                        toRemove.add(entry.getKey());
                        log.debug("Removing ID: " + entry.getKey());
                    });

            processedMessages.keySet().removeAll(toRemove);
            toRemove.clear();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.warn("Sleep interrupted!", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processMessage(AvMessage message) {
        log.debug("Scanning thread: " + Thread.currentThread().getName());

        String virusInfo = null;
        String error = null;
        try {
            byte[] data = message.getData();
            if (data != null && data.length != 0) {
                virusInfo = avService.scanBytesWithInfo(data);
            } else {
                throw new ScanErrorException("No data in the message.");
            }
        } catch (ScanErrorException e) {
            error = e.getMessage();
            log.warn("Scanning error!", e);
        }
        log.debug("Scanning done in: " + Thread.currentThread().getName());

        addProcessedMessage(message.getId());
        removeProcessingMessage(message.getId());
        processedMsgCount.getAndIncrement();

        if (error == null) {
            sendResponse(prepareResponse(message, virusInfo));
        } else {
            sendResponse(prepareErrorResponse(message, error));
        }
    }

    private AvMessage prepareResponse(AvMessage message, String virusInfo) {
        return message.createResponse(virusInfo);
    }

    private AvMessage prepareErrorResponse(AvMessage message, String errorMessage) {
        return message.createErrorResponse(errorMessage);
    }

    private void sendResponse(AvMessage message) {
        notifyListeners(avMessageListeners, message);
    }

    @Override
    public void stop() {
        log.debug("Stopping thread pool...");
        setRunning(false);

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(POOL_TERM_TIME_S, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(POOL_TERM_TIME_S, TimeUnit.SECONDS)) {
                    log.warn("Thread pool termination problem!");
                }
            } else {
                log.debug("Thread pool stopping done.");
            }
        } catch (InterruptedException e) {
            log.warn("Stopping interrupted!", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void addProcessedAVMessageListener(AvMessageListener listener) {
        avMessageListeners.add(listener);
    }

    @Override
    public void removeProcessedAVMessageListener(AvMessageListener listener) {
        avMessageListeners.remove(listener);
    }

    /**
     * Returns actual observer count.
     *
     * @return the observer count
     */
    public int observersCount() {
        return avMessageListeners.size();
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

    private void removeProcessingMessage(String id) {
        processingMessages.remove(id);
    }

    private void addProcessedMessage(String id) {
        processedMessages.put(id, System.currentTimeMillis());
    }

    @ManagedAttribute
    public long getReceivedMsgCount() {
        return receivedMsgCount.get();
    }

    @ManagedAttribute
    public long getProcessedMsgCount() {
        return processedMsgCount.get();
    }
}
