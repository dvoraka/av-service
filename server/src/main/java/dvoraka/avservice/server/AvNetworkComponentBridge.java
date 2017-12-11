package dvoraka.avservice.server;

import dvoraka.avservice.client.transport.AvNetworkComponent;
import dvoraka.avservice.common.listener.AvMessageListener;
import dvoraka.avservice.common.service.ServiceManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

/**
 * AV network component bridge.
 */
@Component
public class AvNetworkComponentBridge implements ServiceManagement {

    private final AvNetworkComponent inComponent;
    private final AvNetworkComponent outComponent;

    private static final Logger log = LogManager.getLogger(AvNetworkComponentBridge.class);

    private AvMessageListener inListener;
    private AvMessageListener outListener;

    private volatile boolean running;
    private volatile boolean started;
    private volatile boolean stopped;


    @Autowired
    public AvNetworkComponentBridge(AvNetworkComponent inComponent, AvNetworkComponent outComponent) {
        this.inComponent = requireNonNull(inComponent);
        this.outComponent = requireNonNull(outComponent);

        if (inComponent.equals(outComponent)) {
            throw new IllegalArgumentException("Components must not be the same!");
        }

        inListener = outComponent::sendMessage;
        outListener = inComponent::sendMessage;
    }

    @Override
    public void start() {
        setStarted(true);
        setStopped(false);

        if (!isRunning()) {
            setRunning(true);

            inComponent.addMessageListener(inListener);
            outComponent.addMessageListener(outListener);
        }
    }

    @Override
    public void stop() {
        log.debug("Bridge stopped.");
        setStopped(true);

        setRunning(false);
        setStarted(false);

        inComponent.removeMessageListener(inListener);
        outComponent.removeMessageListener(outListener);
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
