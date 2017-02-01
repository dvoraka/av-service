package dvoraka.avservice;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.storage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Message processor for processing files.
 */
@Service
public class FileMessageProcessor implements MessageProcessor {

    private final FileService fileService;

    private final List<AvMessageListener> listeners;


    @Autowired
    public FileMessageProcessor(FileService fileService) {
        this.fileService = fileService;
        listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void sendMessage(AvMessage message) {

        if (message.getType() == MessageType.FILE_SAVE) {

            fileService.saveFile(message);
            notifyListeners(listeners, message.createResponse("NNNNN"));

        } else if (message.getType() == MessageType.FILE_LOAD) {

            FileMessage fileMessage = fileService.loadFile(message);
            notifyListeners(listeners, message.createFileResponse(fileMessage.getData()));
        }
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
