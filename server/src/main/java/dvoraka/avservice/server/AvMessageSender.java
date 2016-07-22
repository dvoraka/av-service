package dvoraka.avservice.server;

import dvoraka.avservice.common.data.AvMessage;

/**
 * AV message sender.
 */
@FunctionalInterface
public interface AvMessageSender {

    void sendMessage(AvMessage message);
}
