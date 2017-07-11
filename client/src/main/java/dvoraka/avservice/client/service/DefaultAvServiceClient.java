package dvoraka.avservice.client.service;

import dvoraka.avservice.client.NetworkComponent;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation for the remote AV service.
 */
@Component
public class DefaultAvServiceClient implements AvServiceClient {

    private final NetworkComponent networkComponent;

    private static final Logger log = LogManager.getLogger(DefaultAvServiceClient.class);

    private static final String BAD_TYPE = "Bad message type.";


    @Autowired
    public DefaultAvServiceClient(NetworkComponent networkComponent) {
        this.networkComponent = requireNonNull(networkComponent);
    }

    @Override
    public void checkMessage(AvMessage message) {
        if (!MessageType.FILE_CHECK.equals(message.getType())) {
            log.warn(BAD_TYPE);
            throw new IllegalArgumentException(BAD_TYPE);
        }

        networkComponent.sendAvMessage(message);
    }
}
