package dvoraka.avservice;

import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.DefaultAVMessage;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

/**
 * AVMessage mapper
 */
public class AVMessageMapper {

    public static AVMessage transform(Message msg) {
        MessageProperties msgProps = msg.getMessageProperties();

        return new DefaultAVMessage.Builder(msgProps.getMessageId())
                .data(msg.getBody())
                .build();
    }

    public static Message transform(AVMessage msg) {
        MessageProperties props = new MessageProperties();
        props.setMessageId(msg.getId());
//        props.setCorrelationId(msg.getCorrelationId().getBytes());
        props.setAppId("antivirus");

        props.setHeader("isClean", 0);

        return new Message(msg.getData(), props);
    }
}
