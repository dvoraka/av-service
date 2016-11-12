package dvoraka.avservice.server.checker;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MessageNotFoundException;
import dvoraka.avservice.server.ServerComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * New checker concept.
 */
@Component
public class SimpleChecker implements Checker, AvMessageListener {

    private final ServerComponent component;


    @Autowired
    public SimpleChecker(ServerComponent component) {
        this.component = component;
        this.component.addAvMessageListener(this);
    }


    @Override
    public void sendMessage(AvMessage message) {
        component.sendMessage(message);
    }

    @Override
    public AvMessage receiveMessage(String correlationId) throws MessageNotFoundException {
        return null;
    }

    @Override
    public void onAvMessage(AvMessage message) {
        System.out.println("Message: " + message);
    }
}
