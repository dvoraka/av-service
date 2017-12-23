package dvoraka.avservice.client.transport;

import dvoraka.avservice.common.data.Message;
import dvoraka.avservice.common.helper.MessageHelper;
import dvoraka.avservice.common.listener.MessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Abstract base class for network components.
 *
 * @param <M> the message type
 * @param <L> the listener type
 */
public abstract class AbstractNetworkComponent<M extends Message, L extends MessageListener<M>>
        implements GenericNetworkComponent<M, L>, MessageHelper {

    protected final Logger log = LogManager.getLogger(this.getClass());

    private final Set<L> listeners;


    protected AbstractNetworkComponent() {
        listeners = new CopyOnWriteArraySet<>();
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

    protected Set<L> getListeners() {
        return listeners;
    }
}
