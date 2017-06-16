package dvoraka.avservice.common.data;

/**
 * File message interface.
 */
public interface FileMessage extends Message {

    /**
     * Returns a message data.
     *
     * @return the data
     */
    byte[] getData();

    /**
     * Returns a filename for a data in a message.
     *
     * @return the filename
     */
    String getFilename();

    /**
     * Returns an owner of a data in a message.
     *
     * @return the data owner
     */
    String getOwner();
}
