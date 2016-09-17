package dvoraka.avservice.server;

import org.springframework.amqp.core.MessageListener;

/**
 * Server component.
 */
public interface ServerComponent extends
        AvMessageReceiver,
        AvMessageSender,
        AvMessageNotifier,
        MessageListener,
        javax.jms.MessageListener {
}
