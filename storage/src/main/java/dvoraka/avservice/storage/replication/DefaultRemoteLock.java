package dvoraka.avservice.storage.replication;

/**
 * Default remote lock implementation.
 */
public class DefaultRemoteLock implements RemoteLock {

    @Override
    public boolean lockForFile(String filename, String owner) throws InterruptedException {
        return true;
    }

    @Override
    public boolean unlockForFile(String filename, String owner) {
        return true;
    }
}
