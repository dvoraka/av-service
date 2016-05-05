package dvoraka.avservice.server;

import dvoraka.avservice.common.AVMessageListener;

/**
 * Interface for messages receiving.
 */
public interface AVMessageReceiver {

    void addAVMessageListener(AVMessageListener listener);

    void removeAVMessageListener(AVMessageListener listener);
}
