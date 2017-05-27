package dvoraka.avservice.client.service;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation for a remote file service.
 */
@Component
public class DefaultFileServiceClient implements FileServiceClient {

    private final ServerComponent serverComponent;

    private static final Logger log = LogManager.getLogger(DefaultFileServiceClient.class);
    public static final String BAD_TYPE = "Bad message type.";


    @Autowired
    public DefaultFileServiceClient(ServerComponent serverComponent) {
        this.serverComponent = requireNonNull(serverComponent);
    }

    private void sendMessage(AvMessage message) {
        serverComponent.sendAvMessage(message);
    }

    @Override
    public void saveFile(AvMessage message) {
        if (message.getType() != MessageType.FILE_SAVE) {
            log.warn(BAD_TYPE);
            throw new IllegalArgumentException("Save message type required.");
        }

        sendMessage(message);
    }

    @Override
    public void loadFile(AvMessage message) {
        if (message.getType() != MessageType.FILE_LOAD) {
            log.warn(BAD_TYPE);
            throw new IllegalArgumentException("Load message type required.");
        }

        sendMessage(message);
    }

    @Override
    public void updateFile(AvMessage message) {
        if (message.getType() != MessageType.FILE_UPDATE) {
            log.warn(BAD_TYPE);
            throw new IllegalArgumentException("Update message type required.");
        }

        sendMessage(message);
    }

    @Override
    public void deleteFile(AvMessage message) {
        if (message.getType() != MessageType.FILE_DELETE) {
            log.warn(BAD_TYPE);
            throw new IllegalArgumentException("Delete message type required.");
        }

        sendMessage(message);
    }
}
