package dvoraka.avservice;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.storage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Message processor for files.
 */
@Service
public class FileMessageProcessor implements MessageProcessor {

    private final FileService fileService;


    @Autowired
    public FileMessageProcessor(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public void sendMessage(AvMessage message) {
        fileService.saveFile(message);
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

    }

    @Override
    public void removeProcessedAVMessageListener(AvMessageListener listener) {

    }
}
