package dvoraka.avservice.server;

import dvoraka.avservice.common.data.AvMessage;

/**
 * AV message sender.
 */
@FunctionalInterface
public interface AvMessageSender {

    /**
     * Sends a message.
     *
     * @param message the message
     */
    void sendMessage(AvMessage message);
}
