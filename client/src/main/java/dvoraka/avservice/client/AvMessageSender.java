package dvoraka.avservice.client;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Interface for AV message sending.
 */
@FunctionalInterface
public interface AvMessageSender {

    /**
     * Sends an AV message.
     *
     * @param message the message to send
     */
    void sendAvMessage(AvMessage message);
}
