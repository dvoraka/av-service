package dvoraka.avservice.server.checker;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MessageNotFoundException;
import dvoraka.avservice.server.ServerComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Simple checker class for checking if a transport of messages works. Class has a buffer
 * for incoming messages but if the buffer is full some messages will be lost. Also foreign
 * messages are consumed and thrown out. It is mainly for an infrastructure testing.
 */
@Component
public class SimpleChecker implements Checker, AvMessageListener {

    private static final Logger log = LogManager.getLogger(SimpleChecker.class.getName());

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
        List<AvMessage> savedMessages = new ArrayList<>(QUEUE_CAPACITY);

        AvMessage message;
        while (true) {
            try {
                message = queue.poll(MAX_TIMEOUT, TimeUnit.MILLISECONDS);
                if (message == null) {
                    throw new MessageNotFoundException();
                }

                if (message.getCorrelationId().equals(correlationId)) {
                    for (AvMessage msg : savedMessages) {
                        if (!queue.offer(msg, MAX_TIMEOUT, TimeUnit.MILLISECONDS)) {
                            log.warn("Lost message: " + message);
                        }
                    }
                    savedMessages.clear();

                    return message;
                } else {
                    savedMessages.add(message);
                }
            } catch (InterruptedException e) {
                throw new MessageNotFoundException();
            }
        }
    }

    @Override
    public boolean check() {
        AvMessage normalMessage = Utils.genNormalMessage();
        AvMessage infectedMessage = Utils.genInfectedMessage();

        sendMessage(normalMessage);
        try {
            AvMessage receivedMessage = receiveMessage(normalMessage.getId());

            if (!receivedMessage.getVirusInfo().equals(Utils.OK_VIRUS_INFO)) {
                return false;
            }
        } catch (MessageNotFoundException e) {
            return false;
        }

        sendMessage(infectedMessage);
        try {
            AvMessage receivedMessage = receiveMessage(infectedMessage.getId());

            if (receivedMessage.getVirusInfo().equals(Utils.OK_VIRUS_INFO)) {
                return false;
            }
        } catch (MessageNotFoundException e) {
            return false;
        }

        return true;
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
