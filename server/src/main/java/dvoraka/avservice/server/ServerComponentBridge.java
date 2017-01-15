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

    private volatile boolean running;
    private volatile boolean started;
    private volatile boolean stopped;


    @Autowired
    public ServerComponentBridge(ServerComponent inComponent, ServerComponent outComponent) {
        if (inComponent == outComponent) {
            throw new IllegalArgumentException("Components must not be the same!");
        }

        this.inComponent = inComponent;
        this.outComponent = outComponent;

        inListener = outComponent::sendAvMessage;
        outListener = inComponent::sendAvMessage;
    }

    @Override
    public void start() {
        setStarted(true);
        setStopped(false);

        if (!isRunning()) {
            setRunning(true);

            inComponent.addAvMessageListener(inListener);
            outComponent.addAvMessageListener(outListener);
        }
    }

    @Override
    public void stop() {
        log.debug("Bridge stopped.");
        setStopped(true);

        setRunning(false);
        setStarted(false);

        inComponent.removeAvMessageListener(inListener);
        outComponent.removeAvMessageListener(outListener);
        log.debug("Bridge has stopped");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    private void setStarted(boolean started) {
        this.started = started;
    }

    private void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
