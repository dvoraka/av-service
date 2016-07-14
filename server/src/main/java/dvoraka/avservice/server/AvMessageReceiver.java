package dvoraka.avservice.server;

import dvoraka.avservice.common.AvMessageListener;

/**
 * Interface for messages receiving.
 */
public interface AvMessageReceiver {

    void addAVMessageListener(AvMessageListener listener);

    void removeAVMessageListener(AvMessageListener listener);
}
