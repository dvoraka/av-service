package dvoraka.avservice;

import dvoraka.avservice.data.AVMessage;

/**
 * AV message listener.
 */
public interface AVMessageListener {

    void onAVMessage(AVMessage message);
}
