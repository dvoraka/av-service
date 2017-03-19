package dvoraka.avservice.common.data;

/**
 * Default replication message implementation.
 */
public final class DefaultReplicationMessage implements ReplicationMessage {

    private final String id;
    private final String correlationId;
    private final MessageType type;

    private final byte[] data;
    private final String filename;
    private final String owner;

    private final String fromId;
    private final String toId;
    private final MessageRouting routing;
    private final ReplicationStatus replicationStatus;


    private DefaultReplicationMessage(Builder builder) {
        this.id = builder.id;
        this.correlationId = builder.correlationId;
        this.type = builder.type;

        this.data = builder.data;
        this.filename = builder.filename;
        this.owner = builder.owner;

        this.fromId = builder.fromId;
        this.toId = builder.toId;
        this.routing = builder.routing;
        this.replicationStatus = builder.replicationStatus;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCorrelationId() {
        return correlationId;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public byte[] getData() {
//        return data;
        return null;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public String getFromId() {
        return fromId;
    }

    @Override
    public String getToId() {
        return toId;
    }

    @Override
    public MessageRouting getRouting() {
        return routing;
    }

    @Override
    public ReplicationStatus getReplicationStatus() {
        return replicationStatus;
    }

    @Override
    public QueryType getQueryType() {
        return null;
    }

    public static class Builder {

        private String id;
        private String correlationId;
        private MessageType type;

        private byte[] data;
        private String filename;
        private String owner;

        private String fromId;
        private String toId;
        private MessageRouting routing;
        private ReplicationStatus replicationStatus;

        public Builder() {
        }
    }
}
