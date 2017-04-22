package dvoraka.avservice.client.service;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Anti-virus service client. It is used for client connections to the AV service.
 */
@FunctionalInterface
public interface AvServiceClient {

    /**
     * Checks a file from the message.
     *
     * @param message the AV message
     */
    void checkMessage(AvMessage message);
}
