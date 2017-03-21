package dvoraka.avservice.client.service.response;

import dvoraka.avservice.common.data.ReplicationMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Data structure for messages.
 */
public class ReplicationMessageList {

    private final List<ReplicationMessage> messages;


    public ReplicationMessageList() {
        messages = new ArrayList<>();
    }

    public void add(ReplicationMessage message) {
        messages.add(message);
    }

    public Stream<ReplicationMessage> stream() {
        return messages.stream();
    }
}
