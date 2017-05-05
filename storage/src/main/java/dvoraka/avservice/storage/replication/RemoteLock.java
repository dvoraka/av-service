package dvoraka.avservice.storage.replication;

/**
 * Remote lock interface.
 */
public interface RemoteLock {

    /**
     * Starts the lock.
     */
    void start();

    /**
     * Stops the lock.
     */
    void stop();

    /**
     * Locks a file with a given count and return a result.
     *
     * @param filename  the filename
     * @param owner     the owner
     * @param lockCount the lock count
     * @return the locking result
     * @throws InterruptedException if locking is interrupted
     */
    boolean lockForFile(String filename, String owner, int lockCount) throws InterruptedException;

    /**
     * Unlocks a file.
     *
     * @param filename  the filename
     * @param owner     the owner
     * @param lockCount the lock count
     * @return the unlocking result
     */
    boolean unlockForFile(String filename, String owner, int lockCount);
}
