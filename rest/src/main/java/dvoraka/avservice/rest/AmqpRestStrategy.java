package dvoraka.avservice.rest;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import org.springframework.stereotype.Service;

/**
 * AMQP REST strategy. Receives requests over REST and sends it along over AMQP.
 */
@Service
public class AmqpRestStrategy implements RestStrategy {


    @Override
    public MessageStatus messageStatus(String id) {
        return null;
    }

    @Override
    public MessageStatus messageStatus(String id, String serviceId) {
        return null;
    }

    @Override
    public String messageServiceId(String id) {
        return null;
    }

    @Override
    public void messageCheck(AvMessage message) {
    }

    @Override
    public AvMessage getResponse(String id) {
        return null;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}
