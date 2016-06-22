package dvoraka.avservice.checker.sender;

import java.io.IOException;

/**
 * Anti-virus interface for sending.
 */
public interface AvSender {

    /**
     * Sends file through AMQP.
     *
     * @param virus the dirty flag
     * @param appId the application ID field
     * @return the message ID
     * @throws java.io.IOException
     */
    String sendFile(boolean virus, String appId) throws java.io.IOException;

    /**
     * Purges queue.
     *
     * @param queueName the queue name
     * @throws IOException
     */
    void purgeQueue(String queueName) throws IOException;

    /**
     * Sets global protocol version.
     *
     * @param version the protocol version string
     */
    void setProtocolVersion(String version);

    /**
     * Returns the flag for a verbose output.
     *
     * @return the verbose flag
     */
    boolean isVerboseOutput();

    /**
     * Sets the flag for a verbose output.
     *
     * @param verboseOutput the verbose flag
     */
    void setVerboseOutput(boolean verboseOutput);
}
