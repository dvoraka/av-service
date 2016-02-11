package dvoraka.avservice;

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

    private boolean running;


    @Override
    public void listen() {

        setRunning(true);
        rabbitTemplate.setReceiveTimeout(2000);
        while (isRunning()) {
            System.out.println("Listening...");
            System.out.println(rabbitTemplate.receive());
        }

        System.out.println("Stopping listening...");
    }

    @Override
    public void stop() {
        setRunning(false);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
