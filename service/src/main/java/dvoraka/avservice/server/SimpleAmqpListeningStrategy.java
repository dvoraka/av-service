package dvoraka.avservice.server;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.common.data.AVMessage;
import dvoraka.avservice.common.data.AVMessageMapper;
import dvoraka.avservice.common.exception.MapperException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * Simple AMQP strategy for messages receiving.
 */
public class SimpleAmqpListeningStrategy implements ListeningStrategy {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MessageProcessor messageProcessor;

    private static final Logger log = LogManager.getLogger(SimpleAmqpListeningStrategy.class.getName());

    private boolean running;
    private long listeningTimeout;


    public SimpleAmqpListeningStrategy(long listeningTimeout) {
        this.listeningTimeout = listeningTimeout;
    }

    @Override
    public void listen() {
        log.debug("Listening...");
        setRunning(true);

        while (isRunning()) {

            log.debug("Waiting for a message...");
            Message message = rabbitTemplate.receive();

            if (message != null) {
                log.debug("Message received.");
                try {
                    AVMessage avMessage = AVMessageMapper.transform(message);
                    messageProcessor.sendMessage(avMessage);
                } catch (MapperException e) {
                    log.warn("Message problem!", e);
                }
            }
        }

        log.debug("Listening stopped.");
    }

    @PreDestroy
    @Override
    public void stop() {
        if (isRunning()) {
            log.debug("Stop listening.");
            setRunning(false);

            try {
                TimeUnit.MILLISECONDS.sleep(getListeningTimeout());
            } catch (InterruptedException e) {
                log.warn("Stopping problem!", e);
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    public long getListeningTimeout() {
        return listeningTimeout;
    }
}
