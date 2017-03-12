package dvoraka.avservice.rest.service;

import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.client.service.FileServiceClient;
import dvoraka.avservice.client.service.ResponseClient;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.common.service.BasicMessageStatusStorage;
import dvoraka.avservice.common.service.MessageStatusStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static java.util.Objects.requireNonNull;

/**
 * Remote REST service implementation. Receives requests through REST
 * and sends it along over the network.
 */
@Service
public class RemoteRestService implements RestService {

    private final AvServiceClient avServiceClient;
    private final FileServiceClient fileServiceClient;
    private final ResponseClient responseClient;

    private static final Logger log = LogManager.getLogger(RemoteRestService.class);

    private static final int CACHE_TIMEOUT = 10 * 60 * 1_000;

    private final MessageStatusStorage statusStorage;

    private volatile boolean started;


    @Autowired
    public RemoteRestService(
            AvServiceClient avServiceClient,
            FileServiceClient fileServiceClient,
            ResponseClient responseClient
    ) {
        this.avServiceClient = requireNonNull(avServiceClient);
        this.fileServiceClient = requireNonNull(fileServiceClient);
        this.responseClient = requireNonNull(responseClient);
        statusStorage = new BasicMessageStatusStorage(CACHE_TIMEOUT);
    }

    @Override
    public MessageStatus messageStatus(String id) {
        if (statusStorage.getStatus(id) == MessageStatus.PROCESSING
                && responseClient.getResponse(id) != null) {
            statusStorage.processed(id);
        }

        return statusStorage.getStatus(id);
    }

    @Override
    public void checkMessage(AvMessage message) {
        log.debug("Checking: {}", message);
        statusStorage.started(message.getId());
        avServiceClient.checkMessage(message);
    }

    @Override
    public void saveFile(AvMessage message) {
        log.debug("Saving: {}", message);
        statusStorage.started(message.getId());
        fileServiceClient.saveFile(message);
    }

    @Override
    public void loadFile(AvMessage message) {
        log.debug("Loading: {}", message);
        statusStorage.started(message.getId());
        fileServiceClient.loadFile(message);
    }

    @Override
    public void updateFile(AvMessage message) {
        log.debug("Updating: {}", message);
        statusStorage.started(message.getId());
        fileServiceClient.updateFile(message);
    }

    @Override
    public void deleteFile(AvMessage message) {
        log.debug("Deleting: {}", message);
        statusStorage.started(message.getId());
        fileServiceClient.deleteFile(message);
    }

    @Override
    public AvMessage getResponse(String id) {
        return responseClient.getResponse(id);
    }

    @PostConstruct
    @Override
    public void start() {
        if (isStarted()) {
            log.info("Service is already started.");
            return;
        }

        log.info("Start.");
        setStarted(true);
    }

    @PreDestroy
    @Override
    public void stop() {
        if (!isStarted()) {
            log.info("Service is already stopped.");
            return;
        }

        log.info("Stop.");
        setStarted(false);
        statusStorage.stop();
    }

    public boolean isStarted() {
        return started;
    }

    private void setStarted(boolean started) {
        this.started = started;
    }
}
