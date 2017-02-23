package dvoraka.avservice;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.storage.service.FileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private final Set<AvMessageListener> listeners;
    private final Map<MessageType, Consumer<AvMessage>> processMap;


    @Autowired
    public FileMessageProcessor(FileService fileService) {
        this.fileService = requireNonNull(fileService);
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
        processMap.getOrDefault(message.getType(), this::unknown)
                .accept(message);
    }

    private void save(AvMessage message) {
        fileService.saveFile(message);
        notifyListeners(createOkResponse(message));
    }

    private void load(AvMessage message) {
        FileMessage fileMessage = fileService.loadFile(message);

        notifyListeners(message.createFileResponse(fileMessage.getData(), fileMessage.getType()));
    }

    private void update(AvMessage message) {
        fileService.updateFile(message);
        notifyListeners(createOkResponse(message));
    }

    private void delete(AvMessage message) {
        fileService.deleteFile(message);
        notifyListeners(createOkResponse(message));
    }

    private void unknown(AvMessage message) {
        log.info("Unknown mapping for: " + message);
    }

    private void notifyListeners(AvMessage message) {
        notifyListeners(listeners, message);
    }

    private AvMessage createOkResponse(AvMessage message) {
        return message.createFileResponse(new byte[0], MessageType.FILE_RESPONSE);
    }

    @Override
    public MessageStatus messageStatus(String id) {
        return MessageStatus.UNKNOWN;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
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
