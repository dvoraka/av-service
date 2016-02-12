package dvoraka.avservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Simple AQMP strategy for messages receiving.
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
        rabbitTemplate.setReceiveTimeout(2000);
        while (isRunning()) {
            log.debug("Waiting for a message...");
            System.out.println(rabbitTemplate.receive());
            log.debug("Message received.");

            messageProcessor.sendMessage(new AVMessage() {
                @Override
                public String getId() {
                    return "testId";
                }

                @Override
                public String getCorrelationId() {
                    return "XXX";
                }

                @Override
                public byte[] getData() {
                    return new byte[100];
                }

                @Override
                public AVMessageType getType() {
                    return null;
                }
            });
        }

        log.debug("Listening stopped.");
    }

    @Override
    public void stop() {
        log.debug("Stop listening.");
        setRunning(false);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
