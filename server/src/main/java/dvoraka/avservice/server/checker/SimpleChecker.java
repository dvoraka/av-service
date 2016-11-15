package dvoraka.avservice.server.checker;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MessageNotFoundException;
import dvoraka.avservice.server.ServerComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * New checker concept.
 */
@Component
public class SimpleChecker implements Checker, AvMessageListener {

    private static final long MAX_TIMEOUT = 1_000;
    private static final int QUEUE_CAPACITY = 10;

    private final ServerComponent component;

    private final BlockingQueue<AvMessage> queue;


    @Autowired
    public SimpleChecker(ServerComponent component) {
        this.component = component;
        this.component.addAvMessageListener(this);
        queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    }


    @Override
    public void sendMessage(AvMessage message) {
        component.sendMessage(message);
    }

    @Override
    public AvMessage receiveMessage(String correlationId) throws MessageNotFoundException {
        AvMessage message;
        for (;;) {
            try {
                message = queue.poll(MAX_TIMEOUT, TimeUnit.MILLISECONDS);
                if (message == null) {
                    throw new MessageNotFoundException();
                }

                if (message.getCorrelationId().equals(correlationId)) {
                    return message;
                } else {
                    queue.offer(message, MAX_TIMEOUT, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                throw new MessageNotFoundException();
            }
        }
    }

    @Override
    public void onAvMessage(AvMessage message) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
