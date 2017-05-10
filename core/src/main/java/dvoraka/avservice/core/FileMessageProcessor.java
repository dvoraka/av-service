package dvoraka.avservice.core;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.common.service.BasicMessageStatusStorage;
import dvoraka.avservice.common.service.MessageStatusStorage;
import dvoraka.avservice.storage.FileServiceException;
import dvoraka.avservice.storage.service.FileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * Message processor for processing files.
 */
@Service
public class FileMessageProcessor implements MessageProcessor {

    private final FileService fileService;

    private static final Logger log = LogManager.getLogger(FileMessageProcessor.class);

    public static final int CACHE_TIMEOUT = 10 * 60 * 1_000;
    public static final String FILE_SERVICE_PROBLEM = "File service problem!";

    private final MessageStatusStorage statusStorage;

    private final Set<AvMessageListener> listeners;
    private final Map<MessageType, Consumer<AvMessage>> processMap;


    @Autowired
    public FileMessageProcessor(FileService fileService) {
        this.fileService = requireNonNull(fileService);

        statusStorage = new BasicMessageStatusStorage(CACHE_TIMEOUT);
        listeners = new CopyOnWriteArraySet<>();
        processMap = getCallConfiguration();
    }

    private Map<MessageType, Consumer<AvMessage>> getCallConfiguration() {
        Map<MessageType, Consumer<AvMessage>> configuration = new EnumMap<>(MessageType.class);
        configuration.put(MessageType.FILE_SAVE, this::save);
        configuration.put(MessageType.FILE_LOAD, this::load);
        configuration.put(MessageType.FILE_UPDATE, this::update);
        configuration.put(MessageType.FILE_DELETE, this::delete);

        return Collections.unmodifiableMap(configuration);
    }

    @Override
    public void sendMessage(AvMessage message) {
        log.debug("Receive message: " + message);
        statusStorage.started(message.getId());
        processMap.getOrDefault(message.getType(), this::unknown)
                .accept(message);

        statusStorage.processed(message.getId());
    }

    private void save(AvMessage message) {
        try {
            fileService.saveFile(message);
            notifyListeners(createOkResponse(message));
        } catch (FileServiceException e) {
            log.warn(FILE_SERVICE_PROBLEM, e);
            notifyListeners(message.createErrorResponse("Save problem"));
        }
    }

    private void load(AvMessage message) {
        FileMessage fileMessage;
        try {
            fileMessage = fileService.loadFile(message);
            notifyListeners(message.createFileMessage(
                    fileMessage.getData(), fileMessage.getType()));
        } catch (FileServiceException e) {
            log.warn(FILE_SERVICE_PROBLEM, e);
            notifyListeners(message.createErrorResponse("Load problem"));
        }
    }

    private void update(AvMessage message) {
        try {
            fileService.updateFile(message);
            notifyListeners(createOkResponse(message));
        } catch (FileServiceException e) {
            log.warn(FILE_SERVICE_PROBLEM, e);
            notifyListeners(message.createErrorResponse("Update problem"));
        }
    }

    private void delete(AvMessage message) {
        try {
            fileService.deleteFile(message);
            notifyListeners(createOkResponse(message));
        } catch (FileServiceException e) {
            log.warn(FILE_SERVICE_PROBLEM, e);
            notifyListeners(message.createErrorResponse("Delete problem"));
        }
    }

    private void unknown(AvMessage message) {
        log.warn("Unknown mapping for: " + message);
    }

    private void notifyListeners(AvMessage message) {
        notifyListeners(listeners, message);
    }

    private AvMessage createOkResponse(AvMessage message) {
        return message.createFileMessage(new byte[0], MessageType.FILE_RESPONSE);
    }

    @Override
    public MessageStatus messageStatus(String id) {
        return statusStorage.getStatus(id);
    }

    @PostConstruct
    @Override
    public void start() {
        // do nothing for now
    }

    @PreDestroy
    @Override
    public void stop() {
        statusStorage.stop();
    }

    @Override
    public void addProcessedAVMessageListener(AvMessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeProcessedAVMessageListener(AvMessageListener listener) {
        listeners.remove(listener);
    }
}
