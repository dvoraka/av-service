package dvoraka.avservice.server.client.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.server.ServerComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Default implementation for the remote file service.
 */
@Component
public class DefaultFileServiceClient implements FileServiceClient {

    private final ServerComponent serverComponent;


    @Autowired
    public DefaultFileServiceClient(ServerComponent serverComponent) {
        this.serverComponent = serverComponent;
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
