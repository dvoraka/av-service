package dvoraka.avservice.client.send;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Interface for AV message sending.
 */
@FunctionalInterface
public interface AvMessageSender extends MessageSender<AvMessage> {
}
