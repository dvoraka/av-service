package dvoraka.avservice.storage.replication;

import dvoraka.avservice.common.service.ServiceStatus;

/**
 * Remote lock interface.
 */
public interface RemoteLock extends ServiceStatus {

    /**
     * Starts the lock.
     */
    void start();

    /**
     * Stops the lock.
     */
    void stop();

    /**
     * Locks a file on a given remote node count nodes. Local file is locked too.
     *
     * @param filename  the filename
     * @param owner     the owner
     * @param remoteLockCount the remote lock count
     * @return the locking result
     * @throws InterruptedException if locking is interrupted
     */
    boolean lockForFile(String filename, String owner, int remoteLockCount) throws InterruptedException;

    /**
     * Unlocks a file.
     *
     * @param filename  the filename
     * @param owner     the owner
     * @param lockCount the lock count
     * @return the unlocking result
     */
    boolean unlockForFile(String filename, String owner, int lockCount);

    void networkChanged();
}
