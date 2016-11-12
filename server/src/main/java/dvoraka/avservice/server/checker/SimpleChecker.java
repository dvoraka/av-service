package dvoraka.avservice.server.checker;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MessageNotFoundException;
import dvoraka.avservice.server.ServerComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * New checker concept.
 */
@Component
public class SimpleChecker implements Checker, AvMessageListener {

    private final ServerComponent component;

    private final List<AvMessage> receivedMessages;


    @Autowired
    public SimpleChecker(ServerComponent component) {
        this.component = component;
        this.component.addAvMessageListener(this);

        receivedMessages = new LinkedList<>();
    }


    @Override
    public void sendMessage(AvMessage message) {
        component.sendMessage(message);
    }

    @Override
    public AvMessage receiveMessage(String correlationId) throws MessageNotFoundException {
        while (true) {
            synchronized (receivedMessages) {
                for (AvMessage message : receivedMessages) {

                    if (message.getCorrelationId().equals(correlationId)) {
                        receivedMessages.remove(message);

                        return message;
                    }
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void onAvMessage(AvMessage message) {
        synchronized (receivedMessages) {
            receivedMessages.add(message);
        }
    }
}
