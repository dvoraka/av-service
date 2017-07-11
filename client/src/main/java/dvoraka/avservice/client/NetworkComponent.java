package dvoraka.avservice.client;

/**
 * Network component for sending and receiving AV messages.
 */
public interface NetworkComponent extends
        AvMessageReceiver,
        AvMessageSender,
        MessageListenerAdapter {

    /**
     * Returns a service ID.
     *
     * @return the service ID
     */
    String getServiceId();
}
