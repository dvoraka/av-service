package dvoraka.avservice.server.amqp;

import dvoraka.avservice.common.AVMessageListener;
import dvoraka.avservice.common.data.AVMessage;
import dvoraka.avservice.common.data.AVMessageMapper;
import dvoraka.avservice.common.exception.MapperException;
import dvoraka.avservice.server.ServerComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * AMQP component.
 */
public class AmqpComponent implements ServerComponent {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final Logger log = LogManager.getLogger(AmqpComponent.class.getName());

    private String responseExchange;
    private List<AVMessageListener> listeners = new ArrayList<>();


    public AmqpComponent(String responseExchange) {
        this.responseExchange = responseExchange;
    }

    @Override
    public void onMessage(Message message) {
        AVMessage avMessage = null;
        try {
            avMessage = AVMessageMapper.transform(message);
        } catch (MapperException e) {
            e.printStackTrace();
        }

        for (AVMessageListener listener : listeners) {
            listener.onAVMessage(avMessage);
        }
    }

    @Override
    public void addAVMessageListener(AVMessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeAVMessageListener(AVMessageListener listener) {
        // TODO: implement
    }

    @Override
    public void sendMessage(AVMessage message) {
        try {
            Message response = AVMessageMapper.transform(message);
            rabbitTemplate.send(responseExchange, "ROUTINGKEY", response);
        } catch (MapperException e) {
            log.warn("Message problem!", e);
            // TODO: send error response
        }
    }
}
