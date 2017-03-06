package dvoraka.avservice.server.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.server.ServerComponent;

/**
 * Default implementation for the remote file service.
 */
public class DefaultRemoteFileService implements RemoteFileService {

    private final ServerComponent serverComponent;


    public DefaultRemoteFileService(ServerComponent serverComponent) {
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
    public AvMessage loadFile(AvMessage message) {
        sendMessage(message);
        return null;
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
