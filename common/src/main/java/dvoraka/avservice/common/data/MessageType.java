package dvoraka.avservice.common.data;

/**
 * Message types.
 */
public enum MessageType {
    /**
     * Normal request for a file check.
     */
    REQUEST,
    /**
     * Normal response with check results.
     */
    RESPONSE,
    /**
     * Error response.
     */
    RESPONSE_ERROR,
    /**
     * Check and save a file.
     */
    FILE_SAVE,
    /**
     * Load a file.
     */
    FILE_LOAD,
    /**
     * Check and update a file.
     */
    FILE_UPDATE,
    /**
     * Delete a file.
     */
    FILE_DELETE,
    /**
     * Response with a file status.
     */
    FILE_RESPONSE
}
