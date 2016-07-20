package dvoraka.avservice.server.jms;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.server.ServerComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

/**
 * JMS component.
 */
public class JmsComponent implements ServerComponent {

    @Autowired
    private JmsTemplate jmsTemplate;

    private static final Logger log = LogManager.getLogger(JmsComponent.class.getName());


    @Override
    public void sendMessage(AvMessage message) {

    }

    @Override
    public void addAVMessageListener(AvMessageListener listener) {

    }

    @Override
    public void removeAVMessageListener(AvMessageListener listener) {

    }

    @Override
    public void onMessage(Message message) {

    }
}
