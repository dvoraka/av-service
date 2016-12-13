package dvoraka.avservice.common;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Interface for sending AV message.
 */
public interface AvSender {

    /**
     * Sends AV message.
     *
     * @param message the message to send
     */
    void sendAvMessage(AvMessage message);
}
