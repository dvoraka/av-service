package dvoraka.avservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * Testing JMS client.
 */
public class JmsClient {

    @Autowired
    private JmsTemplate jmsTemplate;


    public void sendTestMessage() {

        MessageCreator messageCreator = session -> session.createTextMessage("Hello!");

        jmsTemplate.send("destination", messageCreator);
    }
}
