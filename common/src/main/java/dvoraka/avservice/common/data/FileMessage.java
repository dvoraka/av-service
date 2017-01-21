package dvoraka.avservice.common.data;

import java.util.UUID;

/**
 * Interface for file message.
 */
public interface FileMessage {

    byte[] getData();

    String getFilename();

    UUID getOwner();
}
