package dvoraka.avservice.common.data;

import dvoraka.avservice.common.data.replication.ReplicationMessage;

/**
 * Enum for command types.
 *
 * @see ReplicationMessage#getCommand()
 */
public enum Command {
    DISCOVER,
    EXISTS,
    LOCK,
    SEQUENCE,
    STATUS,
    UNLOCK,
    FORCE_UNLOCK,

    SAVE,
    LOAD,
    UPDATE,
    DELETE
}
