package dvoraka.avservice.common.data;

/**
 * Replication message.
 */
public interface ReplicationMessage extends FileMessage {

    String getFromId();

    String getToId();

    MessageRouting getRouting();

    ReplicationStatus getReplicationStatus();

    Command getCommand();
}
