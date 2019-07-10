package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.transport.ReplicationComponent;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.replication.ReplicationMessage;
import dvoraka.avservice.common.data.replication.ReplicationStatus;
import dvoraka.avservice.storage.exception.FileServiceException;

/**
 * New replication service.
 */
public class NewReplicationService implements ReplicationService {

    private final ReplicationComponent component;


    public NewReplicationService(ReplicationComponent component) {
        this.component = component;
    }

    @Override
    public ReplicationStatus getStatus(FileMessage message) {
        return null;
    }

    @Override
    public void setReplicationCount(int count) {

    }

    @Override
    public int getMaxResponseTime() {
        return 0;
    }

    @Override
    public void setMaxResponseTime(int maxTime) {

    }

    @Override
    public void onMessage(ReplicationMessage message) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean isStopped() {
        return false;
    }

    @Override
    public void saveFile(FileMessage message) throws FileServiceException {

    }

    @Override
    public FileMessage loadFile(FileMessage message) throws FileServiceException {
        return null;
    }

    @Override
    public void updateFile(FileMessage message) throws FileServiceException {

    }

    @Override
    public void deleteFile(FileMessage message) throws FileServiceException {

    }

    @Override
    public boolean exists(String filename, String owner) {
        return false;
    }
}
