package dvoraka.avservice.core;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.common.listener.AvMessageListener;
import dvoraka.avservice.common.service.BasicMessageStatusStorage;
import dvoraka.avservice.common.service.MessageStatusStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Processor for composition of processors.
 */
@Service
public class CompositeMessageProcessor implements MessageProcessor, AvMessageListener {

    private static final Logger log = LogManager.getLogger(CompositeMessageProcessor.class);

    public static final int CACHE_TIMEOUT = 10 * 60 * 1_000;

    private final List<ProcessorConfiguration> processors;
    private final List<AvMessageListener> listeners;

    private final BlockingQueue<AvMessage> queue;
    private final MessageStatusStorage statusStorage;

    private Predicate<AvMessage> inputFilter;

    private volatile AvMessage actualMessage;


    public CompositeMessageProcessor() {
        processors = new CopyOnWriteArrayList<>();
        listeners = new CopyOnWriteArrayList<>();
        queue = new SynchronousQueue<>();
        statusStorage = new BasicMessageStatusStorage(CACHE_TIMEOUT);
    }

    @Override
    public void sendMessage(AvMessage message) {
        if (inputFilter != null && checkCondition(inputFilter.negate(), message)) {
            log.debug("Filtering out: {}", message);

            return;
        }

        statusStorage.started(message.getId());

        AvMessage lastResult = message;
        for (ProcessorConfiguration configuration : processors) {

            if (!(checkConditions(
                    configuration.getInputConditions().stream(), message, lastResult))) {
                log.debug("Input conditions failed for processor: " + configuration);
                continue;
            }

            // process message in a new thread
            final AvMessage data = configuration.isUseOriginalMessage() ? message : lastResult;
            setActualMessage(data);
            new Thread(() -> configuration.getProcessor().sendMessage(data))
                    .start();

            // wait for result
            try {
                final int waitTime = 5;
                AvMessage result = queue.poll(waitTime, TimeUnit.SECONDS);
                setActualMessage(null);
                notifyListeners(listeners, result);
                lastResult = result;
            } catch (InterruptedException e) {
                log.warn("Polling interrupted!", e);
                Thread.currentThread().interrupt();
                return;
            }
        }

        statusStorage.processed(message.getId());
    }

    @Override
    public MessageStatus messageStatus(String id) {
        return statusStorage.getStatus(id);
    }

    @PostConstruct
    @Override
    public void start() {
        processors.forEach(
                configuration -> configuration.getProcessor()
                        .addProcessedAVMessageListener(this));
    }

    @PreDestroy
    @Override
    public void stop() {
        processors.forEach(
                configuration -> configuration.getProcessor()
                        .stop());
        statusStorage.stop();
    }

    @Override
    public void addProcessedAVMessageListener(AvMessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeProcessedAVMessageListener(AvMessageListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void setInputFilter(Predicate<AvMessage> filter) {
        inputFilter = filter;
    }

    public void addProcessor(ProcessorConfiguration configuration) {
        processors.add(configuration);
    }

    @Override
    public void onMessage(AvMessage message) {
        try {
            if (getActualMessage() != null
                    && message.getCorrelationId().equals(getActualMessage().getId())) {
                queue.put(message);
            }
        } catch (InterruptedException e) {
            log.warn("Putting interrupted!", e);
            Thread.currentThread().interrupt();
        }
    }

    private AvMessage getActualMessage() {
        return actualMessage;
    }

    private void setActualMessage(AvMessage actualMessage) {
        this.actualMessage = actualMessage;
    }
}
