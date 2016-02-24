package dvoraka.avservice;

import dvoraka.avservice.data.AVMessage;

/**
 * Abstraction for message processing.
 */
public interface MessageProcessor {

    void sendMessage(AVMessage message);

    boolean hasProcessedMessage();

    AVMessage getProcessedMessage();

    void stop();

    void addAVMessageListener(AVMessageListener listener);
}
