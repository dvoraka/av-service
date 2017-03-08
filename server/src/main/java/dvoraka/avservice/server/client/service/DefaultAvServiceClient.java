package dvoraka.avservice.server.client.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.server.ServerComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Default implementation for the remote AV service.
 */
@Component
public class DefaultAvServiceClient implements AvServiceClient {

    private final ServerComponent serverComponent;


    @Autowired
    public DefaultAvServiceClient(ServerComponent serverComponent) {
        this.serverComponent = serverComponent;
    }

    @Override
    public void checkMessage(AvMessage message) {
        serverComponent.sendAvMessage(message);
    }
}
