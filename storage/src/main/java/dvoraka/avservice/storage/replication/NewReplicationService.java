package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.transport.ReplicationComponent;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.replication.ReplicationMessage;
import dvoraka.avservice.common.data.replication.ReplicationStatus;
import dvoraka.avservice.common.listener.ReplicationMessageListener;
import dvoraka.avservice.common.service.TimedStorage;
import dvoraka.avservice.storage.exception.FileServiceException;
import dvoraka.avservice.storage.service.FileService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static java.util.Objects.requireNonNull;

/**
 * New replication service.
 */
public class NewReplicationService implements ReplicationService {

    private final FileService fileService;
    private final ReplicationComponent component;

    private final TimedStorage<String> openFiles;

    private volatile boolean running;
    private final ReplicationMessageListener messageListener;


    public NewReplicationService(FileService fileService, ReplicationComponent component) {
        this.fileService = requireNonNull(fileService);
        this.component = requireNonNull(component);

        openFiles = new TimedStorage<>();
        messageListener = this;
    }

    @PostConstruct
    @Override
    public void start() {
        component.addMessageListener(messageListener);
        running = true;
    }

    @PreDestroy
    @Override
    public void stop() {
        component.removeMessageListener(messageListener);
        running = false;
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
    public boolean isRunning() {
        return running;
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

    private boolean localCopyExists(FileMessage message) {
        return localCopyExists(message.getFilename(), message.getOwner());
    }

    private boolean localCopyExists(String filename, String owner) {
        return fileService.exists(filename, owner);
    }
}
