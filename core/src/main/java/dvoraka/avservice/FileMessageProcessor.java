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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * Message processor for processing files.
 */
@Service
public class FileMessageProcessor implements MessageProcessor {

    private final FileService fileService;

    private static final Logger log = LogManager.getLogger(FileMessageProcessor.class);

    private final List<AvMessageListener> listeners;
    private final Map<MessageType, Consumer<AvMessage>> processMap;


    @Autowired
    public FileMessageProcessor(FileService fileService) {
        this.fileService = requireNonNull(fileService);
        listeners = new CopyOnWriteArrayList<>();
        processMap = getCallConfiguration();
    }

    private Map<MessageType, Consumer<AvMessage>> getCallConfiguration() {
        Map<MessageType, Consumer<AvMessage>> configuration = new HashMap<>();
        configuration.put(MessageType.FILE_SAVE, this::save);
        configuration.put(MessageType.FILE_LOAD, this::load);
        configuration.put(MessageType.FILE_UPDATE, this::update);
        configuration.put(MessageType.FILE_DELETE, this::delete);

        return configuration;
    }

    @Override
    public void sendMessage(AvMessage message) {
        log.debug("Receive message: " + message);
        processMap.get(message.getType())
                .accept(message);
    }

    private void save(AvMessage message) {
        fileService.saveFile(message);

        notifyListeners(createOkResponse(message));
    }

    private void load(AvMessage message) {
        FileMessage fileMessage = fileService.loadFile(message);

        notifyListeners(message.createFileResponse(fileMessage.getData()));
    }

    private void update(AvMessage message) {
        fileService.updateFile(message);

        notifyListeners(createOkResponse(message));
    }

    private void delete(AvMessage message) {
        fileService.deleteFile(message);

        notifyListeners(createOkResponse(message));
    }

    private void notifyListeners(AvMessage message) {
        notifyListeners(listeners, message);
    }

    private AvMessage createOkResponse(AvMessage message) {
        return message.createFileResponse(new byte[0]);
    }

    @Override
    public MessageStatus messageStatus(String id) {
        return null;
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
