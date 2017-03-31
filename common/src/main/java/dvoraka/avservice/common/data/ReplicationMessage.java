package dvoraka.avservice.common.data;

/**
 * Replication message.
 */
public interface ReplicationMessage extends FileMessage {

    String getFromId();

    String getToId();

    long getSequence();

    MessageRouting getRouting();

    ReplicationStatus getReplicationStatus();

    Command getCommand();
}
