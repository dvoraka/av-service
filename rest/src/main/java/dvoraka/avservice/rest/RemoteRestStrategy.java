package dvoraka.avservice.rest;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.server.ServerComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Remote REST strategy. Receives requests over REST and sends it along over network.
 */
@Service
public class RemoteRestStrategy implements RestStrategy, AvMessageListener {

    private final ServerComponent serverComponent;


    @Autowired
    public RemoteRestStrategy(ServerComponent serverComponent) {
        this.serverComponent = serverComponent;
    }

    @Override
    public MessageStatus messageStatus(String id) {
        return null;
    }

    @Override
    public MessageStatus messageStatus(String id, String serviceId) {
        return null;
    }

    @Override
    public String messageServiceId(String id) {
        return null;
    }

    @Override
    public void messageCheck(AvMessage message) {
        serverComponent.sendMessage(message);
    }

    @Override
    public AvMessage getResponse(String id) {
        return null;
    }

    @Override
    public void start() {
        serverComponent.addAvMessageListener(this);
    }

    @Override
    public void stop() {
        serverComponent.removeAvMessageListener(this);
    }

    @Override
    public void onAvMessage(AvMessage message) {
        System.out.println("REST on message");
    }
}
