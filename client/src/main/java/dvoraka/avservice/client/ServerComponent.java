package dvoraka.avservice.client;

import dvoraka.avservice.common.AvMessageHelper;
import dvoraka.avservice.common.MessageListenerAdapter;

/**
 * Server component.
 */
public interface ServerComponent extends
        AvMessageReceiver,
        AvMessageSender,
        AvMessageHelper,
        MessageListenerAdapter {
}
