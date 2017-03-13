package dvoraka.avservice.common;

import dvoraka.avservice.common.data.ReplicationMessage;

/**
 * Message listener.
 */
@FunctionalInterface
public interface ReplicationMessageListener {

    void onMessage(ReplicationMessage message);
}
