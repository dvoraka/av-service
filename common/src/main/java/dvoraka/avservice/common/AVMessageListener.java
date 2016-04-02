package dvoraka.avservice.common;

import dvoraka.avservice.common.data.AVMessage;

/**
 * AV message listener.
 */
public interface AVMessageListener {

    void onAVMessage(AVMessage message);
}
