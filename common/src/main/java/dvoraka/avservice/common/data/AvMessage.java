package dvoraka.avservice.common.data;

//TODO

/**
 * Data structure for the whole AV service.
 * <br>
 * <br>
 * Data:
 * <ul>
 * <li>ID - message UUID</li>
 * <li>correlationId - correlation UUID</li>
 * <li>type - message type</li>
 * <li>data - message data</li>
 * <li>filename - file name</li>
 * <ln>owner - owner of file</ln>
 * <li>virusInfo - info about infection</li>
 * </ul>
 */
public interface AvMessage extends FileMessage {

    String getVirusInfo();

    AvMessage createResponse(String virusInfo);

    AvMessage createErrorResponse(String errorMessage);

    AvMessage createFileResponse(byte[] data, MessageType type);
}
