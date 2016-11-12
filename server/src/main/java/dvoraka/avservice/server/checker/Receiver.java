package dvoraka.avservice.server.checker;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MessageNotFoundException;

/**
 * Interface for receiving AV message.
 */
public interface Receiver {

    /**
     * Receives a message with the given correlationId.
     *
     * @param correlationId the correlation ID
     * @return the AV message with the given correlationId
     * @throws MessageNotFoundException when it is not possible to find the message
     */
    AvMessage receiveMessage(String correlationId) throws MessageNotFoundException;
}
