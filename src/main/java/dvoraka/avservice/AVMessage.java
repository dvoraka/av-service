package dvoraka.avservice;

/**
 * Data structure for the whole AV service.
 */
public interface AVMessage {

    String getId();

    String getCorrelationId();

    byte[] getData();

    AVMessageType getType();
}
