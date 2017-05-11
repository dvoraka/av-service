package dvoraka.avservice.client.service;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.common.data.AvMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation for the remote file service.
 */
//TODO: add type checking
@Component
public class DefaultFileServiceClient implements FileServiceClient {

    private final ServerComponent serverComponent;


    @Autowired
    public DefaultFileServiceClient(ServerComponent serverComponent) {
        this.serverComponent = requireNonNull(serverComponent);
    }

    private void sendMessage(AvMessage message) {
        serverComponent.sendAvMessage(message);
    }

    @Override
    public void saveFile(AvMessage message) {
        sendMessage(message);
    }

    @Override
    public void loadFile(AvMessage message) {
        sendMessage(message);
    }

    @Override
    public void updateFile(AvMessage message) {
        sendMessage(message);
    }

    @Override
    public void deleteFile(AvMessage message) {
        sendMessage(message);
    }
}
