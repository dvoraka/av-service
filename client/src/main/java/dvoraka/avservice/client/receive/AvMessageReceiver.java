package dvoraka.avservice.client.receive;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.listener.AvMessageListener;

/**
 * Interface for AV message receiving.
 */
public interface AvMessageReceiver extends MessageReceiver<AvMessage, AvMessageListener> {
}
