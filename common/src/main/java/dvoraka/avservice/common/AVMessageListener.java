package dvoraka.avservice.common;

import dvoraka.avservice.common.data.AvMessage;

/**
 * AV message listener.
 */
public interface AVMessageListener {

    void onAVMessage(AvMessage message);
}
