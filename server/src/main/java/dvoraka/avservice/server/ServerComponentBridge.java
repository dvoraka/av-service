package dvoraka.avservice.server;

import dvoraka.avservice.common.service.ServiceManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Server component bridge.
 */
@Component
public class ServerComponentBridge implements ServiceManagement {

    private final ServerComponent inComponent;
    private final ServerComponent outComponent;

    private boolean running;


    @Autowired
    public ServerComponentBridge(ServerComponent inComponent, ServerComponent outComponent) {
        this.inComponent = inComponent;
        this.outComponent = outComponent;
    }

    @Override
    public void start() {
        if (!running) {
            running = true;
            inComponent.addAvMessageListener(outComponent::sendMessage);
            outComponent.addAvMessageListener(inComponent::sendMessage);
        }
    }

    @Override
    public void stop() {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
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
}
