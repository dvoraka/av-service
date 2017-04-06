package dvoraka.avservice.client.service.response;

import dvoraka.avservice.common.data.ReplicationMessage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * Data structure for messages.
 */
public class ReplicationMessageList {

    private final List<ReplicationMessage> messages;


    public ReplicationMessageList() {
        //TODO
        messages = new CopyOnWriteArrayList<>();
    }

    public void add(ReplicationMessage message) {
        messages.add(message);
    }

    public Stream<ReplicationMessage> stream() {
        return messages.stream();
    }
}
