package dvoraka.avservice.client;

import dvoraka.avservice.common.data.Message;
import dvoraka.avservice.common.helper.MessageHelper;
import dvoraka.avservice.common.listener.MessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Abstract base class for network components.
 */
public abstract class AbstractNetworkComponent<M extends Message, L extends MessageListener<M>>
        implements GenericNetworkComponent<M, L>, MessageHelper {

    protected final Logger log = LogManager.getLogger(this.getClass());

    private final List<L> listeners;


    protected AbstractNetworkComponent() {
        listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void addMessageListener(L listener) {
        listeners.add(listener);
    }

    @Override
    public void removeMessageListener(L listener) {
        listeners.remove(listener);
    }

    @Override
    public int getListenerCount() {
        return listeners.size();
    }

    protected List<L> getListeners() {
        return listeners;
    }
}
