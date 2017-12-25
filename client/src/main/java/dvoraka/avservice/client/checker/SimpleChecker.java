package dvoraka.avservice.client.checker;

import dvoraka.avservice.client.transport.AvNetworkComponent;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MessageNotFoundException;
import dvoraka.avservice.common.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.requireNonNull;

/**
 * Simple checker for the checking if a transport of messages works. Foreign
 * messages are consumed and thrown out. It is mainly for an infrastructure testing.
 */
@Component
public class SimpleChecker implements Checker {

    private final AvNetworkComponent avNetworkComponent;

    private static final Logger log = LogManager.getLogger(SimpleChecker.class);

    private static final long MAX_TIMEOUT = 3_000;

    private final HashMap<String, AvMessage> messages;
    private final Lock lock;
    private final Condition newMessage;


    @Autowired
    public SimpleChecker(AvNetworkComponent avNetworkComponent) {
        this.avNetworkComponent = requireNonNull(avNetworkComponent);
        this.avNetworkComponent.addMessageListener(this::onMessage);

        messages = new HashMap<>();
        lock = new ReentrantLock();
        newMessage = lock.newCondition();
    }

    @Override
    public void sendMessage(AvMessage message) {
        avNetworkComponent.sendMessage(message);
    }

    @Override
    public AvMessage receiveMessage(String correlationId) throws MessageNotFoundException {

        long start = System.currentTimeMillis();
        while (true) {

            lock.lock();
            try {
                if (messages.containsKey(correlationId)) {
                    AvMessage message = messages.get(correlationId);
                    messages.remove(correlationId);

                    return message;
                }

                if (System.currentTimeMillis() - start > MAX_TIMEOUT) {
                    throw new MessageNotFoundException();
                }

                final int maxWaitTime = 20;
                newMessage.await(maxWaitTime, TimeUnit.MILLISECONDS);

            } catch (InterruptedException e) {
                log.warn("Receiving interrupted!");
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public boolean check() {
        AvMessage normalMessage = Utils.genMessage();
        AvMessage infectedMessage = Utils.genInfectedMessage();

        sendMessage(normalMessage);
        sendMessage(infectedMessage);

        try {
            AvMessage receivedMessage = receiveMessage(normalMessage.getId());

            if (isInfected(receivedMessage)) {
                return false;
            }
        } catch (MessageNotFoundException e) {
            log.debug("Message not found.", e);
            return false;
        }

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

    private void onMessage(AvMessage message) {
        lock.lock();
        try {
            messages.put(message.getCorrelationId(), message);
            newMessage.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean getAsBoolean() {
        return check();
    }
}
