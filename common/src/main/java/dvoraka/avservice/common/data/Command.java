package dvoraka.avservice.common.data;

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
