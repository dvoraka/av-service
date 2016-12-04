package dvoraka.avservice.rest;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.server.ServerComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AMQP REST strategy. Receives requests over REST and sends it along over AMQP.
 */
@Service
public class AmqpRestStrategy implements RestStrategy, AvMessageListener {

    private final ServerComponent amqpComponent;


    @Autowired
    public AmqpRestStrategy(ServerComponent amqpComponent) {
        this.amqpComponent = amqpComponent;
    }

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
        amqpComponent.sendMessage(message);
    }

    @Override
    public AvMessage getResponse(String id) {
        return null;
    }

    @Override
    public void start() {
        amqpComponent.addAvMessageListener(this);
    }

    @Override
    public void stop() {
        amqpComponent.removeAvMessageListener(this);
    }

    @Override
    public void onAvMessage(AvMessage message) {
        System.out.println("REST on message");
    }
}
