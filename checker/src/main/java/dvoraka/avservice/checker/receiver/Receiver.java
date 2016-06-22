package dvoraka.avservice.checker.receiver;

import dvoraka.avservice.checker.exception.LastMessageException;
import dvoraka.avservice.checker.exception.ProtocolException;

import java.io.IOException;

/**
 * AMQP interface for receiving.
 */
public interface Receiver {

    /**
     * Receives an info about the message.
     *
     * @param corrId the message ID
     * @throws IOException
     * @throws InterruptedException
     * @throws ProtocolException
     * @throws LastMessageException
     */
    boolean receive(String corrId) throws
            IOException,
            InterruptedException,
            ProtocolException,
            LastMessageException;

    boolean isVerboseOutput();

    void setVerboseOutput(boolean verboseOutput);
}
