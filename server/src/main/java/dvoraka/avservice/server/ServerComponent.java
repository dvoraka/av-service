package dvoraka.avservice.server;

import org.springframework.amqp.core.MessageListener;

/**
 * Server component.
 */
public interface ServerComponent extends
        AvMessageReceiver,
        AvMessageSender,
        MessageListener,
        javax.jms.MessageListener {
}
