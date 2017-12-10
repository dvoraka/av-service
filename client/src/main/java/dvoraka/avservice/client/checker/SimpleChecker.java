package dvoraka.avservice.client.checker;

import dvoraka.avservice.client.NetworkComponent;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MessageNotFoundException;
import dvoraka.avservice.common.listener.AvMessageListener;
import dvoraka.avservice.common.util.Utils;
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

    private final NetworkComponent networkComponent;

    private static final Logger log = LogManager.getLogger(SimpleChecker.class.getName());

    private static final long MAX_TIMEOUT = 3_000;
    private static final int QUEUE_CAPACITY = 10;

    private final BlockingQueue<AvMessage> queue;


    @Autowired
    public SimpleChecker(NetworkComponent networkComponent) {
        this(networkComponent, QUEUE_CAPACITY);
    }

    public SimpleChecker(NetworkComponent networkComponent, int queueSize) {
        this.networkComponent = requireNonNull(networkComponent);
        this.networkComponent.addMessageListener(this);
        queue = new ArrayBlockingQueue<>(queueSize);
    }

    @Override
    public void sendMessage(AvMessage message) {
        networkComponent.sendMessage(message);
    }

    @Override
    public AvMessage receiveMessage(String correlationId) throws MessageNotFoundException {
        List<AvMessage> savedMessages = new ArrayList<>(QUEUE_CAPACITY);

        long start = System.currentTimeMillis();
        AvMessage message;
        while (true) {
            try {
                if (System.currentTimeMillis() - start > MAX_TIMEOUT) {
                    returnMessagesToQueue(savedMessages);

                    throw new MessageNotFoundException();
                }

                message = queue.poll(MAX_TIMEOUT, TimeUnit.MILLISECONDS);
                if (message == null) {
                    returnMessagesToQueue(savedMessages);

                    throw new MessageNotFoundException();
                }

                if (message.getCorrelationId().equals(correlationId)) {
                    returnMessagesToQueue(savedMessages);

                    return message;
                }

                savedMessages.add(message);

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

        sendMessage(normalMessage);
        try {
            AvMessage receivedMessage = receiveMessage(normalMessage.getId());

            if (isInfected(receivedMessage)) {
                return false;
            }
        } catch (MessageNotFoundException e) {
            log.debug("Message not found.", e);
            return false;
        }

        sendMessage(infectedMessage);
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
    public void onMessage(AvMessage message) {
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
