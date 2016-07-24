package dvoraka.avservice.server.jms;

import dvoraka.avservice.common.data.AvMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.TextMessage;

/**
 * Testing JMS client.
 */
public class JmsClient {

    @Autowired
    private JmsTemplate jmsTemplate;

    public static final String TEST_DESTINATION = "test-destination";
    public static final String TEST_TEXT = "HELLO!";
    public static final String TEST_CORRELATION_ID = "TEST1";


    public void sendTestMessage() {
        jmsTemplate.send(TEST_DESTINATION, getTestMessageCreator());
    }

    public MessageCreator getTestMessageCreator() {
        return session -> {
            TextMessage msg = session.createTextMessage(TEST_TEXT);
            msg.setJMSCorrelationID(TEST_CORRELATION_ID);
            return msg;
        };
    }

    public String receiveTestMessage() {
        return jmsTemplate.receiveAndConvert(TEST_DESTINATION).toString();
    }

    public Object receiveTestMessageAsObject() {
        return jmsTemplate.receive(TEST_DESTINATION);
    }

    public void sendMessage(AvMessage message, String destination) {
        jmsTemplate.convertAndSend(destination, message);
    }

    public AvMessage receiveMessage(String destination) {
        return (AvMessage) jmsTemplate.receiveAndConvert(destination);
    }
}
