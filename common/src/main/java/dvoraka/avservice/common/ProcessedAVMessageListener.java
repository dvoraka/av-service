package dvoraka.avservice.common;

import dvoraka.avservice.common.data.AVMessage;

/**
 * Processed AV message listener.
 */
public interface ProcessedAVMessageListener {

    void onProcessedAVMessage(AVMessage message);
}
