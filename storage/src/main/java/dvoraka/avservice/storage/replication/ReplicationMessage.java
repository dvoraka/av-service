package dvoraka.avservice.storage.replication;

import dvoraka.avservice.common.data.FileMessage;

/**
 * Replication message.
 */
public interface ReplicationMessage extends FileMessage {

    String getFromId();

    String getToId();

    MessageRouting getRouting();
}
