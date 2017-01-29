package dvoraka.avservice.common.data;

/**
 * Data structure for the whole AV service.
 * <br>
 * <br>
 * Data:
 * <ul>
 * <li>ID - message UUID</li>
 * <li>correlationId - correlation UUID</li>
 * <li>data - message data</li>
 * <li>type - message type</li>
 * <li>serviceId - service ID</li>
 * <li>virusInfo - info about infection</li>
 * </ul>
 */
public interface AvMessage extends FileMessage {

    AvMessageType getType();

    String getServiceId();

    String getVirusInfo();

    AvMessage createResponse(String virusInfo);

    AvMessage createErrorResponse(String errorMessage);
}
