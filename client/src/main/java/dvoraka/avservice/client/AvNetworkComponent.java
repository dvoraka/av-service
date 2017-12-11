package dvoraka.avservice.client;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.listener.AvMessageListener;

/**
 * Av network component for sending and receiving AV messages.
 */
public interface AvNetworkComponent extends GenericNetworkComponent<AvMessage, AvMessageListener> {
}
