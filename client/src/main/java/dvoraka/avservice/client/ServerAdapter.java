package dvoraka.avservice.client;

/**
 * Adapter for sending and receiving AV messages.
 */
public interface ServerAdapter extends
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
