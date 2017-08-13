package dvoraka.avservice.common.data;

/**
 * Message types.
 */
public enum MessageType {
    /**
     * Check a file for viruses.
     */
    FILE_CHECK,
    /**
     * Check response with check results.
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
     * File not found.
     */
    FILE_NOT_FOUND,
    /**
     * Response with a file status.
     */
    FILE_RESPONSE,
    /**
     * Replication normal command.
     */
    REPLICATION_COMMAND,
    /**
     * Replication service command.
     */
    REPLICATION_SERVICE,
    /**
     * Special message for a network diagnostics.
     */
    DIAGNOSTICS
}
