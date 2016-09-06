package dvoraka.avservice.common;

import dvoraka.avservice.common.data.AvMessage;

/**
 * AV message listener.
 */
@FunctionalInterface
public interface AvMessageListener {

    /**
     * Receives messages.
     *
     * @param message the message
     */
    void onAvMessage(AvMessage message);
}
