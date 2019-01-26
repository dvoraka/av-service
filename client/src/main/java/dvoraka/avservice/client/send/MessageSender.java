package dvoraka.avservice.client.send;

import dvoraka.avservice.common.data.Message;

/**
 * Interface for generic message sending.
 *
 * @param <M> the message type
 */
@FunctionalInterface
public interface MessageSender<M extends Message> {

    /**
     * Sends a message.
     *
     * @param message the message
     */
    void send(M message);
}
