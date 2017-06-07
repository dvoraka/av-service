package dvoraka.avservice.storage.replication.exception;

import dvoraka.avservice.storage.exception.FileServiceException;

/**
 * Exception for not acquiring a lock.
 */
public class CannotAcquireLockException extends FileServiceException {
    private static final long serialVersionUID = 6230026048508659576L;
}
