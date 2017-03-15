package dvoraka.avservice.client;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Interface for sending AV message.
 */
@FunctionalInterface
public interface AvMessageSender {

    /**
     * Sends AV message.
     *
     * @param message the message to send
     */
    void sendAvMessage(AvMessage message);
}
