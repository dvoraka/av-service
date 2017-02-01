package dvoraka.avservice.common.data;

/**
 * Message interface.
 */
public interface Message {

    String getId();

    String getCorrelationId();

    MessageType getType();
}
