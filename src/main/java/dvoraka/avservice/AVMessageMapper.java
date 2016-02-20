package dvoraka.avservice;

import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.DefaultAVMessage;
import org.springframework.amqp.core.Message;

/**
 * AVMessage mapper
 */
public class AVMessageMapper {

    public static AVMessage transform(Message msg) {
        return new DefaultAVMessage(msg.getBody());
    }
}
