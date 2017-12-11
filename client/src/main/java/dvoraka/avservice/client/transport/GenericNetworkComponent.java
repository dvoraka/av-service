package dvoraka.avservice.client.transport;

import dvoraka.avservice.client.MessageListenerAdapter;
import dvoraka.avservice.client.receive.MessageReceiver;
import dvoraka.avservice.client.send.MessageSender;
import dvoraka.avservice.common.data.Message;
import dvoraka.avservice.common.listener.MessageListener;

/**
 * Generic network component for sending and receiving messages.
 *
 * @param <M> the message type
 * @param <L> the listener type
 */
public interface GenericNetworkComponent<M extends Message, L extends MessageListener<M>> extends
        MessageSender<M>,
        MessageReceiver<M, L>,
        MessageListenerAdapter {

    /**
     * Returns a service ID.
     *
     * @return the service ID
     */
    String getServiceId();
}
