package dvoraka.avservice.client;

import dvoraka.avservice.common.helper.AvMessageHelper;

/**
 * Component for sending and receiving AV messages.
 */
public interface ServerComponent extends
        AvMessageReceiver,
        AvMessageSender,
        AvMessageHelper,
        MessageListenerAdapter {

    /**
     * Returns a service ID.
     *
     * @return the ID
     */
    String getServiceId();
}
