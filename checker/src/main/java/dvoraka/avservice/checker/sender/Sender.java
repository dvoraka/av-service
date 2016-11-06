package dvoraka.avservice.checker.sender;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Interface for sending AV message.
 */
public interface Sender {

    /**
     * Sends AV message.
     *
     * @param message the message to send
     */
    void sendMessage(AvMessage message);
}
