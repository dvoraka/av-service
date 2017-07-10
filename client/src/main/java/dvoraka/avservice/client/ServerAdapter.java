package dvoraka.avservice.client;

import dvoraka.avservice.common.helper.AvMessageHelper;

/**
 * Adapter for sending and receiving AV messages.
 */
public interface ServerAdapter extends
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
