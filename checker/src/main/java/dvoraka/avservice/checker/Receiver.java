package dvoraka.avservice.checker;

import java.io.IOException;

/**
 * AMQP interface for receiving.
 *
 * @author dvoraka
 */
public interface Receiver {

    /**
     * Receives data.
     *
     * @param corrId parent message ID
     * @throws IOException
     * @throws InterruptedException
     * @throws dvoraka.avservice.checker.BadExchangeException
     */
    boolean receive(String corrId) throws
            java.io.IOException,
            InterruptedException,
            ProtocolException,
            LastMessageException;

    boolean getVerboseOutput();

    void setVerboseOutput(boolean verboseOutput);
}
