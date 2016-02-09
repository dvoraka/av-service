package dvoraka.avservice;

/**
 * Abstraction for message processing.
 */
public interface MessageProcessor {

    void sendMessage(AVMessage message);

    boolean hasProcessedMessage();

    AVMessage getProcessedMessage();
}
