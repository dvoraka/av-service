package dvoraka.avservice.server;

import dvoraka.avservice.common.data.AVMessage;

/**
 * AV message sender.
 */
public interface AVMessageSender {

    void sendMessage(AVMessage message);
}
