package dvoraka.avservice.server;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.service.ServiceManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Server components bridge.
 */
@Component
public class ServerComponentBridge implements ServiceManagement {

    private static final Logger log = LogManager.getLogger(ServerComponentBridge.class);

    private final ServerComponent inComponent;
    private final ServerComponent outComponent;

    private AvMessageListener inListener;
    private AvMessageListener outListener;

    private boolean running;


    @Autowired
    public ServerComponentBridge(ServerComponent inComponent, ServerComponent outComponent) {
        if (inComponent == outComponent) {
            throw new IllegalArgumentException("Components must not be the same!");
        }

        this.inComponent = inComponent;
        this.outComponent = outComponent;

        inListener = outComponent::sendMessage;
        outListener = inComponent::sendMessage;
    }

    @Override
    public void start() {
        if (!running) {
            running = true;
            inComponent.addAvMessageListener(inListener);
            outComponent.addAvMessageListener(outListener);
        }
    }

    @Override
    public void stop() {
        log.debug("Bridge stopped.");
        setRunning(false);

        inComponent.removeAvMessageListener(inListener);
        outComponent.removeAvMessageListener(outListener);

        log.debug("Bridge has stopped");
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isStarted() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isStopped() {
        throw new UnsupportedOperationException("Not implemented");
    }

    private void setRunning(boolean running) {
        this.running = running;
    }
}
