package dvoraka.avservice.server;

import dvoraka.avservice.common.AvMessageHelper;
import dvoraka.avservice.common.AvMessageSender;
import org.springframework.amqp.core.MessageListener;

/**
 * Server component.
 */
public interface ServerComponent extends
        AvMessageReceiver,
        AvMessageSender,
        AvMessageHelper,
        MessageListener,
        javax.jms.MessageListener {
}
