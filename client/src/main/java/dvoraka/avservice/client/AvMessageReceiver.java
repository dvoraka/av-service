package dvoraka.avservice.client;

import dvoraka.avservice.common.AvMessageListener;

/**
 * Interface for messages receiving.
 */
public interface AvMessageReceiver {

    void addAvMessageListener(AvMessageListener listener);

    void removeAvMessageListener(AvMessageListener listener);
}
