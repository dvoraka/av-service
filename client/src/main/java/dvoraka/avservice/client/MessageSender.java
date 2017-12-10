package dvoraka.avservice.client;

import dvoraka.avservice.common.data.Message;

public interface MessageSender<T extends Message> {

    /**
     * Sends a message.
     *
     * @param message the message
     */
    void sendMessage(T message);
}
