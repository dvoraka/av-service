package dvoraka.avservice.storage.replication;

import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.replication.ReplicationStatus;
import dvoraka.avservice.common.helper.ExecutorServiceHelper;
import dvoraka.avservice.common.listener.ReplicationMessageListener;
import dvoraka.avservice.common.service.ServiceStatus;
import dvoraka.avservice.storage.service.FileService;

/**
 * Replication service interface.
 */
public interface ReplicationService extends
        FileService,
        ExecutorServiceHelper,
        ReplicationMessageListener,
        ServiceStatus {

    /**
     * Returns a status for the file.
     *
     * @param message the file message
     * @return the status
     */
    ReplicationStatus getStatus(FileMessage message);

    /**
     * Sets the replication count for the service.
     *
     * @param count the number of replicas
     */
    void setReplicationCount(int count);

    /**
     * Returns the maximum response time for the service.
     *
     * @return the maximum response time
     */
    int getMaxResponseTime();

    /**
     * Sets the maximum response time for the service.
     *
     * @param maxTime the maximum response time
     */
    void setMaxResponseTime(int maxTime);
}
