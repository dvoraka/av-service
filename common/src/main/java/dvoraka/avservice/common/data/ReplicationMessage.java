package dvoraka.avservice.common.data;

/**
 * Replication message interface.
 */
public interface ReplicationMessage extends FileMessage {

    /**
     * Returns a message source.
     *
     * @return the node ID
     */
    String getFromId();

    /**
     * Returns a message destination
     *
     * @return the node ID
     */
    String getToId();

    /**
     * Returns a sequence.
     *
     * @return the sequence
     */
    long getSequence();

    /**
     * Returns a message routing type.
     *
     * @return the routing type
     */
    MessageRouting getRouting();

    /**
     * Returns data about a replication status.
     *
     * @return the replication status
     */
    ReplicationStatus getReplicationStatus();

    /**
     * Returns a message command
     *
     * @return the command
     */
    Command getCommand();

    /**
     * Returns a file message.
     *
     * @return the message
     */
    FileMessage fileMessage();
}
