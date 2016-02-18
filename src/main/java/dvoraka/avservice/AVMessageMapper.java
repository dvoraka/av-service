package dvoraka.avservice;

import org.springframework.amqp.core.Message;

/**
 * AVMessage mapper
 */
public class AVMessageMapper {

    public static AVMessage transform(Message msg) {
        return new DefaultAVMessage(msg.getBody());
    }
}
