package dvoraka.avservice.server;

import dvoraka.avservice.common.AVMessageListener;
import dvoraka.avservice.common.data.AVMessage;
import dvoraka.avservice.common.data.AVMessageMapper;
import dvoraka.avservice.common.exception.MapperException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.util.ArrayList;
import java.util.List;

/**
 * WIP AMQP structure
 */
public class AmqpComponent implements MessageListener {

    private List<AVMessageListener> listeners = new ArrayList<>();


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

    public void addAVMessageListener(AVMessageListener listener) {
        listeners.add(listener);
    }
}
