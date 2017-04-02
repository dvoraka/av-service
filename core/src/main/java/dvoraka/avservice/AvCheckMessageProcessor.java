package dvoraka.avservice;

import dvoraka.avservice.avprogram.service.AvService;
import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.CustomThreadFactory;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.common.exception.ScanException;
import dvoraka.avservice.common.service.BasicMessageStatusStorage;
import dvoraka.avservice.common.service.MessageStatusStorage;
import dvoraka.avservice.db.service.MessageInfoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.requireNonNull;

/**
 * AV message processor. Checks messages for viruses.
 */
@Service
@ManagedResource
public class AvCheckMessageProcessor implements MessageProcessor {

    private final AvService avService;
    private final MessageInfoService messageInfoService;

    private static final Logger log = LogManager.getLogger(AvCheckMessageProcessor.class);

    public static final int CACHE_TIMEOUT = 10 * 60 * 1_000;
    private static final long POOL_TERM_TIME_S = 20;
    private static final AvMessageSource MESSAGE_SOURCE = AvMessageSource.PROCESSOR;

    private final MessageStatusStorage statusStorage;

    private final AtomicLong receivedMsgCount = new AtomicLong();
    private final AtomicLong processedMsgCount = new AtomicLong();

    private final Set<AvMessageListener> avMessageListeners;

    private final ExecutorService executorService;

    private final int threadCount;
    private final String serviceId;

    private volatile boolean running;


    /**
     * Creates a processor with a given thread count, service ID, anti-virus service and
     * message info service.
     *
     * @param threadCount        the processing thread count
     * @param serviceId          the service ID string
     * @param avService          the anti-virus service
     * @param messageInfoService the message info service
     */
    @Autowired
    public AvCheckMessageProcessor(
            int threadCount,
            String serviceId,
            AvService avService,
            MessageInfoService messageInfoService
    ) {
        this.threadCount = threadCount;
        this.serviceId = serviceId;
        this.avService = requireNonNull(avService);
        this.messageInfoService = requireNonNull(messageInfoService);

        ThreadFactory threadFactory = new CustomThreadFactory("check-message-processor-");
        executorService = Executors.newFixedThreadPool(threadCount, threadFactory);

        statusStorage = new BasicMessageStatusStorage(CACHE_TIMEOUT);

        avMessageListeners = new CopyOnWriteArraySet<>();
    }

    @PostConstruct
    @Override
    public void start() {
        if (isRunning()) {
            return;
        }

        setRunning(true);
        log.info("Message processor started.");
    }

    @PreDestroy
    @Override
    public void stop() {
        if (!isRunning()) {
            return;
        }

        log.debug("Stopping processor...");
        setRunning(false);
        statusStorage.stop();

        shutdownAndAwaitTermination(executorService, POOL_TERM_TIME_S, log);
        log.debug("Processor stopped.");
    }

    @Override
    public void sendMessage(AvMessage message) {
        receivedMsgCount.getAndIncrement();

        log.debug("Processing message...");
        statusStorage.started(message.getId());

        messageInfoService.save(message, MESSAGE_SOURCE, serviceId);

        executorService.execute(() -> processMessage(message));
        log.debug("Message accepted.");
    }

    @Override
    public MessageStatus messageStatus(String id) {
        log.debug("Message status call from: " + Thread.currentThread().getName());

        return statusStorage.getStatus(id);
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
                throw new ScanException("No data in the message.");
            }
        } catch (ScanException e) {
            error = requireNonNull(e.getMessage());
            log.warn("Scanning error!", e);
        }
        log.debug("Scanning done in: " + Thread.currentThread().getName());

        statusStorage.processed(message.getId());

        processedMsgCount.getAndIncrement();

        if (error == null) {
            sendResponse(prepareResponse(message, virusInfo));
        } else {
            sendResponse(prepareErrorResponse(message, error));
        }
    }

    private void sendResponse(AvMessage message) {
        notifyListeners(avMessageListeners, message);
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

    @ManagedAttribute
    public long getReceivedMsgCount() {
        return receivedMsgCount.get();
    }

    @ManagedAttribute
    public long getProcessedMsgCount() {
        return processedMsgCount.get();
    }
}
