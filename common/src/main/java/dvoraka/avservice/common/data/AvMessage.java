package dvoraka.avservice.common.data;

/**
 * AV message interface.
 * <p>
 * Data structure for the whole service.
 * <br>
 * <br>
 * Data:
 * <ul>
 * <li>id - message UUID</li>
 * <li>correlationId - correlation UUID</li>
 * <li>type - message type</li>
 * <li>data - message data</li>
 * <li>filename - filename of the data</li>
 * <li>owner - owner of file/data</li>
 * <li>virusInfo - info about infection in data</li>
 * </ul>
 */
public interface AvMessage extends FileMessage {

    /**
     * Returns an info about virus inside the message.
     *
     * @return the info
     */
    String getVirusInfo();

    /**
     * Creates an AV check response to a check message.
     *
     * @param virusInfo the virus info
     * @return the AV check message response
     */
    AvMessage createCheckResponse(String virusInfo);

    /**
     * Creates an error response to a message. Good for an error signaling.
     *
     * @param errorMessage the error message
     * @return the response message
     */
    AvMessage createErrorResponse(String errorMessage);

    /**
     * Creates a file message response to a message.
     *
     * @param data the data for the response
     * @param type the type of the message
     * @return the new file message
     */
    AvMessage createFileMessage(byte[] data, MessageType type);
}
