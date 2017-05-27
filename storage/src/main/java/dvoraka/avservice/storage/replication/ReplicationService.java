package dvoraka.avservice.storage.replication;

import dvoraka.avservice.common.ReplicationMessageListener;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.ReplicationStatus;
import dvoraka.avservice.common.service.ExecutorServiceHelper;
import dvoraka.avservice.storage.service.FileService;

/**
 * Replication service interface.
 */
public interface ReplicationService extends
        FileService,
        ExecutorServiceHelper,
        ReplicationMessageListener {

    ReplicationStatus getStatus(FileMessage message);

    void setReplicationCount(int count);
}
