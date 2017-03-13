package dvoraka.avservice.storage.replication;

import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.storage.service.FileService;

/**
 * Replication service interface.
 */
public interface ReplicationService extends FileService {

    ReplicationStatus getStatus(FileMessage message);
}
