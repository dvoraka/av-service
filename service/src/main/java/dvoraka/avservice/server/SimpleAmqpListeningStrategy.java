package dvoraka.avservice.server;

import dvoraka.avservice.exception.MapperException;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.AVMessageMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;

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
        }
    }

    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }
}
