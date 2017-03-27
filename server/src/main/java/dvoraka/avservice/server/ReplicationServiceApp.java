package dvoraka.avservice.server;

import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.storage.replication.ReplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Replication service app.
 */
@Service
public class ReplicationServiceApp implements ServiceManagement {

    private final ReplicationService replicationService;


    @Autowired
    public ReplicationServiceApp(ReplicationService replicationService) {
        this.replicationService = replicationService;
    }

    @Override
    public void start() {
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean isStopped() {
        return false;
    }
}
