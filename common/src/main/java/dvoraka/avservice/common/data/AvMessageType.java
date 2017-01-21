package dvoraka.avservice.common.data;

/**
 * AV message types.
 */
public enum AvMessageType {
    /**
     * Normal request for file check.
     */
    REQUEST,
    /**
     * Request for file check and save.
     */
    FILE_REQUEST,
    /**
     * Normal response with check results.
     */
    RESPONSE,
    /**
     * Response with file saving status.
     */
    FILE_RESPONSE,
    /**
     * Error response.
     */
    RESPONSE_ERROR
}
