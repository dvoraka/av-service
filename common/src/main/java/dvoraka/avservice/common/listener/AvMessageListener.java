package dvoraka.avservice.common.listener;

import dvoraka.avservice.common.data.AvMessage;

/**
 * AV message listener.
 */
@FunctionalInterface
public interface AvMessageListener extends MessageListener<AvMessage> {
}
