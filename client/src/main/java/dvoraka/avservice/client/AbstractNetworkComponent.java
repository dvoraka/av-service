package dvoraka.avservice.client;

import dvoraka.avservice.common.AvMessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Abstract base class for network components.
 */
public abstract class AbstractNetworkComponent implements NetworkComponent {

    protected final Logger log = LogManager.getLogger(this.getClass());

    private final List<AvMessageListener> listeners;


    protected AbstractNetworkComponent() {
        listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void addAvMessageListener(AvMessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeAvMessageListener(AvMessageListener listener) {
        listeners.remove(listener);
    }

    @Override
    public int getListenerCount() {
        return listeners.size();
    }

    protected List<AvMessageListener> getListeners() {
        return listeners;
    }
}
