package dvoraka.avservice.storage.replication;

import dvoraka.avservice.common.service.ServiceManagement;
import org.springframework.stereotype.Service;

/**
 * Common service app class.
 */
//TODO
@Service
public class CommonServiceApp implements ServiceManagement {

    private volatile boolean running;
    private volatile boolean started;
    private volatile boolean stopped;


    @Override
    public void start() {
        started = true;
        running = true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void stop() {
        stopped = true;
        running = false;
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
