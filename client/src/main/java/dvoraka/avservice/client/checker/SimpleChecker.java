package dvoraka.avservice.client.checker;

import dvoraka.avservice.client.transport.AvNetworkComponent;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MessageNotFoundException;
import dvoraka.avservice.common.listener.AvMessageListener;
import dvoraka.avservice.common.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * Simple checker for the checking if a transport of messages works. Foreign
 * messages are consumed and thrown out. It is mainly for an infrastructure testing.
 */
@Component
public class SimpleChecker implements Checker, AvMessageListener {

    private final AvNetworkComponent avNetworkComponent;

    private static final Logger log = LogManager.getLogger(SimpleChecker.class);

    private static final long MAX_TIMEOUT = 3_000;

    private final ConcurrentHashMap<String, AvMessage> messages;


    @Autowired
    public SimpleChecker(AvNetworkComponent avNetworkComponent) {
        this.avNetworkComponent = requireNonNull(avNetworkComponent);
        this.avNetworkComponent.addMessageListener(this);
        messages = new ConcurrentHashMap<>();
    }

    @Override
    public void sendMessage(AvMessage message) {
        avNetworkComponent.sendMessage(message);
    }

    @Override
    public AvMessage receiveMessage(String correlationId) throws MessageNotFoundException {

        long start = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - start > MAX_TIMEOUT) {
                throw new MessageNotFoundException();
            }

            if (messages.containsKey(correlationId)) {
                AvMessage message = messages.get(correlationId);
                messages.remove(correlationId);

                return message;
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
        messages.put(message.getCorrelationId(), message);
    }

    @Override
    public boolean getAsBoolean() {
        return check();
    }
}
