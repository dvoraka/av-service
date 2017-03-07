package dvoraka.avservice.server.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.server.ServerComponent;

/**
 * Default implementation for the remote file service.
 */
public class DefaultFileServiceClient implements FileServiceClient {

    private final ServerComponent serverComponent;


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

    @Override
    public AvMessage getResponse(String id) {
        //TODO
        return null;
    }
}
