package dvoraka.avservice.server.jms;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.server.ServerComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
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
        if (message == null) {
            throw new IllegalArgumentException("Message may not be null!");
        }

        AvMessage avMessage;
        try {
            avMessage = (AvMessage) messageConverter.fromMessage(message);
        } catch (JMSException | MessageConversionException e) {
            log.warn("Conversion error!", e);

            return;
        }

        notifyListeners(listeners, avMessage);
    }

    @Override
    public void sendMessage(AvMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Message may not be null!");
        }

        jmsTemplate.convertAndSend(responseDestination, message);
    }

    @Override
    public synchronized void addAvMessageListener(AvMessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public synchronized void removeAvMessageListener(AvMessageListener listener) {
        listeners.remove(listener);
    }

    public int listenersCount() {
        return listeners.size();
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
