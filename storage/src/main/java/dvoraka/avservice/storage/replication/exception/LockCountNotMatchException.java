package dvoraka.avservice.storage.replication.exception;

import dvoraka.avservice.storage.exception.FileServiceException;

/**
 * Exception for signaling wrong lock count match.
 */
public class LockCountNotMatchException extends FileServiceException {
    private static final long serialVersionUID = -3144400424720011856L;
}
