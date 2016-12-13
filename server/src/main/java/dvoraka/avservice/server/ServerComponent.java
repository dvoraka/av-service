package dvoraka.avservice.server;

import dvoraka.avservice.common.AvMessageNotifier;
import dvoraka.avservice.common.AvMessageSender;
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
