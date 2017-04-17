package dvoraka.avservice.storage.replication;

import dvoraka.avservice.common.service.ServiceManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Replication service app.
 */
@Service
public class ReplicationServiceApp implements ServiceManagement {

    private final ReplicationService replicationService;

    private volatile boolean running;
    private volatile boolean started;
    private volatile boolean stopped;


    @Autowired
    public ReplicationServiceApp(ReplicationService replicationService) {
        this.replicationService = replicationService;
    }

    @Override
    public void start() {
        running = true;
        started = true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void stop() {
        running = false;
        stopped = true;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }
}
