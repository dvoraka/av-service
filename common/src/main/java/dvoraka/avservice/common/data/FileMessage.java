package dvoraka.avservice.common.data;

/**
 * Interface for file message.
 */
public interface FileMessage {

    byte[] getData();

    String getFilename();

    String getOwner();
}
