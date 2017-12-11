package dvoraka.avservice.client;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.listener.AvMessageListener;

/**
 * Network component for sending and receiving AV messages.
 */
public interface NetworkComponent extends GenericNetworkComponent<AvMessage, AvMessageListener> {
}
