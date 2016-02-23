package dvoraka.avservice.server;

import dvoraka.avservice.MapperException;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.AVMessageMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Parallel AMQP strategy prototype for messages receiving.
 */
public class ParallelAmqpListeningStrategy implements ListeningStrategy {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MessageProcessor messageProcessor;

    private static final Logger log = LogManager.getLogger(ParallelAmqpListeningStrategy.class.getName());

    private boolean running;
    private int listeners;
    private ExecutorService executorService;


    public ParallelAmqpListeningStrategy(int listeners) {
        this.listeners = listeners;
        executorService = Executors.newFixedThreadPool(listeners);
    }

    @Override
    public void listen() {
        log.debug("Listening...");
        setRunning(true);

        for (int i = 0; i < listeners; i++) {
            executorService.execute(this::receiveAndProcess);
        }
    }

    private void receiveAndProcess() {
        while (isRunning()) {

            log.debug("Waiting for a message...");
            Message message = rabbitTemplate.receive();

            if (message != null) {
                log.debug("Message received.");
                try {
                    AVMessage avMessage = AVMessageMapper.transform(message);
                    messageProcessor.sendMessage(avMessage);
                    log.debug("Message sent.");
                } catch (MapperException e) {
                    log.warn("Message problem!", e);
                }
            }
        }

        log.debug("Listening stopped.");
    }

    @Override
    public void stop() {
        log.debug("Stop listening.");
        setRunning(false);
        executorService.shutdown();

        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Stopping problem!", e);
        }
    }

    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    public int getListenersCount() {
        return listeners;
    }
}
