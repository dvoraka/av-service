package dvoraka.avservice.server.jms;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.server.ServerComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * JMS component.
 */
public class JmsComponent implements ServerComponent {

    @Autowired
    private JmsTemplate jmsTemplate;

    private static final Logger log = LogManager.getLogger(JmsComponent.class.getName());

    private String destination = JmsClient.TEST_DESTINATION;
    private List<AvMessageListener> listeners = new ArrayList<>();


    /**
     * AMQP method.
     *
     * @param message
     */
    @Override
    public void onMessage(Message message) {
    }

    @Override
    public void onMessage(javax.jms.Message message) {
        System.out.println("Message received.");
    }

    @Override
    public void sendMessage(AvMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Message may not be null!");
        }

        jmsTemplate.convertAndSend(destination, message);
    }

    @Override
    public void addAVMessageListener(AvMessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeAVMessageListener(AvMessageListener listener) {
        listeners.remove(listener);
    }
}
