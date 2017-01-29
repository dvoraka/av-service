package dvoraka.avservice.common.data;

/**
 * Interface for file message.
 */
public interface FileMessage {

    String getId();

    String getCorrelationId();

    byte[] getData();

    String getFilename();

    String getOwner();
}
