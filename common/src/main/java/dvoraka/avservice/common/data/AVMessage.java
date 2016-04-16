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
public interface AVMessage {

    String getId();

    String getCorrelationId();

    byte[] getData();

    AVMessageType getType();

    String getServiceId();

    String getVirusInfo();

    AVMessage createResponse(boolean virus);
}
