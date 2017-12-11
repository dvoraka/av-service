package dvoraka.avservice.server;

import dvoraka.avservice.client.send.AvMessageSender;
import dvoraka.avservice.common.listener.AvMessageListener;
import dvoraka.avservice.common.service.ServiceManagement;

/**
 * Server interface.
 */
public interface AvServer extends ServiceManagement, AvMessageListener, AvMessageSender {
}
