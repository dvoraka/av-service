package dvoraka.avservice;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.storage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        listeners = new ArrayList<>();
    }

    @Override
    public void sendMessage(AvMessage message) {
        fileService.saveFile(message);

        notifyListeners(listeners, message);
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
