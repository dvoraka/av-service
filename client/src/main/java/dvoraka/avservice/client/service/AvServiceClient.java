package dvoraka.avservice.client.service;

import dvoraka.avservice.common.data.AvMessage;

import java.util.concurrent.Future;

/**
 * Anti-virus service client. It is used for client connections to the AV service.
 */
@FunctionalInterface
public interface AvServiceClient {

    /**
     * Checks a data from a message.
     *
     * @param message the AV message
     * @return future with the response
     * @see dvoraka.avservice.common.data.MessageType#FILE_CHECK
     */
    Future<AvMessage> checkMessage(AvMessage message);
}
