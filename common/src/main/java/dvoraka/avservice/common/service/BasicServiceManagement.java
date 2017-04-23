package dvoraka.avservice.common.service;

import org.springframework.stereotype.Service;

/**
 * Basic service management implementation for the simplest usage.
 */
@Service
public class BasicServiceManagement implements ServiceManagement {

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
