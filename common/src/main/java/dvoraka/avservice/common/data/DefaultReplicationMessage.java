package dvoraka.avservice.common.data;

import static java.util.Objects.requireNonNull;

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
    private final QueryType queryType;


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
        this.queryType = builder.queryType;
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
        return data.clone();
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
        return queryType;
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
        private QueryType queryType;


        public Builder(String id) {
            this.id = requireNonNull(id);
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder type(MessageType type) {
            this.type = type;
            return this;
        }

        public Builder data(byte[] data) {
            if (data != null) {
                this.data = data.clone();
            }
            return this;
        }

        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder fromId(String fromId) {
            this.fromId = fromId;
            return this;
        }

        public Builder toId(String toId) {
            this.toId = toId;
            return this;
        }

        public Builder routing(MessageRouting routing) {
            this.routing = routing;
            return this;
        }

        public Builder replicationStatus(ReplicationStatus replicationStatus) {
            this.replicationStatus = replicationStatus;
            return this;
        }

        public Builder queryType(QueryType queryType) {
            this.queryType = queryType;
            return this;
        }

        public DefaultReplicationMessage build() {
            if (data == null) {
                data = new byte[0];
            }

            return new DefaultReplicationMessage(this);
        }
    }
}
