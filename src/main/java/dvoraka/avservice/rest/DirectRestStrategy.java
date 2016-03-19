package dvoraka.avservice.rest;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.MessageStatus;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation for a local calling.
 */
public class DirectRestStrategy implements RestStrategy {

    @Autowired
    private MessageProcessor messageProcessor;


    @Override
    public MessageStatus messageStatus(String id) {
        return messageProcessor.messageStatus(id);
    }

    @Override
    public MessageStatus messageStatus(String id, String serviceId) {
        return messageStatus(id);
    }

    @Override
    public String messageServiceId(String id) {
        return null;
    }

    @Override
    public void messageCheck(AVMessage message) {
        messageProcessor.sendMessage(message);
    }

    @Override
    public AVMessage getResponse(String id) {
        // TODO: check ID
        if (messageProcessor.hasProcessedMessage()) {
            return messageProcessor.getProcessedMessage();
        } else {
            return null;
        }
    }

    @Override
    public void stop() {
        messageProcessor.stop();
    }
}
