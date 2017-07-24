package dvoraka.avservice.client.checker;

import dvoraka.avservice.client.NetworkComponent;
import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MessageNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * Simple checker class for checking if a transport of messages works. Class has a buffer
 * for incoming messages but if the buffer is full some messages will be lost. Also foreign
 * messages are consumed and thrown out. It is mainly for an infrastructure testing.
 */
@Component
public class SimpleChecker implements Checker, AvMessageListener {

    private final NetworkComponent component;

    private static final Logger log = LogManager.getLogger(SimpleChecker.class.getName());

    private static final long MAX_TIMEOUT = 3_000;
    private static final int QUEUE_CAPACITY = 10;

    private final BlockingQueue<AvMessage> queue;


    @Autowired
    public SimpleChecker(NetworkComponent component) {
        this(component, QUEUE_CAPACITY);
    }

    public SimpleChecker(NetworkComponent component, int queueSize) {
        this.component = requireNonNull(component);
        this.component.addAvMessageListener(this);
        queue = new ArrayBlockingQueue<>(queueSize);
    }

    @Override
    public void sendAvMessage(AvMessage message) {
        component.sendAvMessage(message);
    }

    @Override
    public AvMessage receiveMessage(String correlationId) throws MessageNotFoundException {
        List<AvMessage> savedMessages = new ArrayList<>(QUEUE_CAPACITY);

        AvMessage message;
        while (true) {
            try {
                message = queue.poll(MAX_TIMEOUT, TimeUnit.MILLISECONDS);
                if (message == null) {
                    throw new MessageNotFoundException();
                }

                if (message.getCorrelationId().equals(correlationId)) {

                    returnMessagesToQueue(savedMessages);
                    savedMessages.clear();

                    return message;
                } else {
                    savedMessages.add(message);
                }
            } catch (InterruptedException e) {
                log.warn("Waiting interrupted!", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void returnMessagesToQueue(List<AvMessage> messages)
            throws InterruptedException {
        for (AvMessage msg : messages) {
            if (!queue.offer(msg, MAX_TIMEOUT, TimeUnit.MILLISECONDS)) {
                log.warn("Lost message: " + msg);
            }
        }
    }

    @Override
    public boolean check() {
        AvMessage normalMessage = Utils.genMessage();
        AvMessage infectedMessage = Utils.genInfectedMessage();

        sendAvMessage(normalMessage);
        try {
            AvMessage receivedMessage = receiveMessage(normalMessage.getId());

            if (isInfected(receivedMessage)) {
                return false;
            }
        } catch (MessageNotFoundException e) {
            log.debug("Message not found.", e);
            return false;
        }

        sendAvMessage(infectedMessage);
        try {
            AvMessage receivedMessage = receiveMessage(infectedMessage.getId());

            if (!isInfected(receivedMessage)) {
                return false;
            }
        } catch (MessageNotFoundException e) {
            log.debug("Message not found.", e);
            return false;
        }

        return true;
    }

    private boolean isInfected(AvMessage message) {
        return !message.getVirusInfo().equals(Utils.OK_VIRUS_INFO);
    }

    @Override
    public void onAvMessage(AvMessage message) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            log.warn("Waiting interrupted!", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean getAsBoolean() {
        return check();
    }
}
