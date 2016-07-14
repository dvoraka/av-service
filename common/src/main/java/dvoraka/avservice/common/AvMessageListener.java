package dvoraka.avservice.common;

import dvoraka.avservice.common.data.AvMessage;

/**
 * AV message listener.
 */
public interface AvMessageListener {

    void onAVMessage(AvMessage message);
}
