package dvoraka.avservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Testing JMS client.
 */
public class JmsClient {

    @Autowired
    JmsTemplate jmsTemplate;


    public void sendTestMessage() {

        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("Hello!");
            }
        };

        jmsTemplate.send("destination", messageCreator);
    }
}
