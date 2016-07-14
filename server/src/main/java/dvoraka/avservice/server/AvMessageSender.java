package dvoraka.avservice.server;

import dvoraka.avservice.common.data.AvMessage;

/**
 * AV message sender.
 */
public interface AvMessageSender {

    void sendMessage(AvMessage message);
}
