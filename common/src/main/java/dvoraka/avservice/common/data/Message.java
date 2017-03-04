package dvoraka.avservice.common.data;

/**
 * Message interface.
 */
public interface Message {

    /**
     * Returns ID of a message.
     *
     * @return the message ID
     */
    String getId();

    /**
     * Returns a correlation ID of a message.
     *
     * @return the message correlation ID
     */
    String getCorrelationId();

    /**
     * Returns a message type.
     *
     * @return the message type
     */
    MessageType getType();
}
