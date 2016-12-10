package dvoraka.avservice.rest;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.server.ServerComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Remote REST strategy. Receives requests over REST and sends it along over network.
 */
@Service
public class RemoteRestStrategy implements RestStrategy, AvMessageListener {

    private final ServerComponent serverComponent;

    private static final Logger log = LogManager.getLogger(RemoteRestStrategy.class);

    private final ConcurrentHashMap<String, Long> processingMsgs = new ConcurrentHashMap<>();


    @Autowired
    public RemoteRestStrategy(ServerComponent serverComponent) {
        this.serverComponent = serverComponent;
    }

    @Override
    public MessageStatus messageStatus(String id) {
        return messageStatus(id, null);
    }

    @Override
    public MessageStatus messageStatus(String id, String serviceId) {
        if (processingMsgs.containsKey(id)) {
            return MessageStatus.PROCESSING;
        }

        return null;
    }

    @Override
    public String messageServiceId(String id) {
        return null;
    }

    @Override
    public void messageCheck(AvMessage message) {
        log.debug("Checking: {}", message);
        processingMsgs.put(message.getId(), System.currentTimeMillis());
        serverComponent.sendMessage(message);
    }

    @Override
    public AvMessage getResponse(String id) {
        return null;
    }

    @PostConstruct
    @Override
    public void start() {
        log.info("Started.");
        serverComponent.addAvMessageListener(this);
    }

    @PreDestroy
    @Override
    public void stop() {
        log.info("Stopped.");
        serverComponent.removeAvMessageListener(this);
    }

    @Override
    public void onAvMessage(AvMessage message) {
        log.info("REST on message: {}", message);
    }
}
