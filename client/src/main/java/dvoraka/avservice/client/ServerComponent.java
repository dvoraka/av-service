package dvoraka.avservice.client;

import dvoraka.avservice.common.AvMessageHelper;

/**
 * Server component.
 */
public interface ServerComponent extends
        AvMessageReceiver,
        AvMessageSender,
        AvMessageHelper,
        MessageListenerAdapter {

    String getServiceId();
}
