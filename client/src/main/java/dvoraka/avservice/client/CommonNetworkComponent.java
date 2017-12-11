package dvoraka.avservice.client;

import dvoraka.avservice.common.data.Message;
import dvoraka.avservice.common.listener.MessageListener;

/**
 * Common network component for sending and receiving messages.
 */
public interface CommonNetworkComponent<M extends Message, L extends MessageListener<M>> extends
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
