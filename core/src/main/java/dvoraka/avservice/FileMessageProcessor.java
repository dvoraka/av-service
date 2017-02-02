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
        this.fileService = fileService;
        listeners = new CopyOnWriteArrayList<>();
        processMap = getCallConfiguration();
    }

    private Map<MessageType, Consumer<AvMessage>> getCallConfiguration() {
        Map<MessageType, Consumer<AvMessage>> configuration = new HashMap<>();
        configuration.put(MessageType.FILE_SAVE, this::safe);
        configuration.put(MessageType.FILE_LOAD, this::load);

        return configuration;
    }

    @Override
    public void sendMessage(AvMessage message) {
        processMap.get(message.getType())
                .accept(message);
    }

    private void safe(AvMessage message) {
        fileService.saveFile(message);
        notifyListeners(listeners, message.createFileResponse(new byte[0]));
    }

    private void load(AvMessage message) {
        FileMessage fileMessage = fileService.loadFile(message);
        notifyListeners(listeners, message.createFileResponse(fileMessage.getData()));
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

    }
}
