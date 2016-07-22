package dvoraka.avservice.server.jms;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.server.ServerComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;

/**
 * JMS component.
 */
public class JmsComponent implements ServerComponent {

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private MessageConverter messageConverter;

    private static final Logger log = LogManager.getLogger(JmsComponent.class.getName());

    private String responseDestination;
    private List<AvMessageListener> listeners = new ArrayList<>();


    public JmsComponent(String responseDestination) {
        this.responseDestination = responseDestination;
    }

    @Override
    public void onMessage(Message message) {
        AvMessage avMessage = null;
        try {
            avMessage = (AvMessage) messageConverter.fromMessage(message);
        } catch (JMSException e) {
            log.warn("On message error!", e);
        }

        for (AvMessageListener listener : listeners) {
            listener.onAVMessage(avMessage);
        }
    }

    @Override
    public void sendMessage(AvMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Message may not be null!");
        }

        jmsTemplate.convertAndSend(responseDestination, message);
    }

    @Override
    public void addAVMessageListener(AvMessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeAVMessageListener(AvMessageListener listener) {
        listeners.remove(listener);
    }

    /**
     * AMQP method.
     *
     * @param message
     */
    @Override
    public void onMessage(org.springframework.amqp.core.Message message) {
        throw new UnsupportedOperationException();
    }
}
