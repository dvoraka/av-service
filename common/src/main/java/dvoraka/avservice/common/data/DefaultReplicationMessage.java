package dvoraka.avservice.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import dvoraka.avservice.common.helper.FileServiceHelper;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

/**
 * Default replication message implementation.
 */
@JsonDeserialize(builder = DefaultReplicationMessage.Builder.class)
public final class DefaultReplicationMessage implements ReplicationMessage, FileServiceHelper {

    private final String id;
    private final String correlationId;
    private final MessageType type;

    private final byte[] data;
    private final String filename;
    private final String owner;

    private final String fromId;
    private final String toId;
    private final long sequence;
    private final MessageRouting routing;
    private final ReplicationStatus replicationStatus;
    private final Command command;


    private DefaultReplicationMessage(Builder builder) {
        this.id = builder.id;
        this.correlationId = builder.correlationId;
        this.type = builder.type;

        this.data = builder.data;
        this.filename = builder.filename;
        this.owner = builder.owner;

        this.fromId = builder.fromId;
        this.toId = builder.toId;
        this.sequence = builder.sequence;
        this.routing = builder.routing;
        this.replicationStatus = builder.replicationStatus;
        this.command = builder.command;
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
    public long getSequence() {
        return sequence;
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
    public Command getCommand() {
        return command;
    }

    @Override
    public FileMessage fileMessage() {
        if (getType() != MessageType.REPLICATION_COMMAND) {
            return null;
        }

        switch (getCommand()) {
            case SAVE:
                return fileSaveMessage(getFilename(), getOwner(), getData());

            default:
                return null;
        }
    }

    @Override
    @SuppressWarnings("checkstyle:OperatorWrap")
    public String toString() {

        final int maxDataLength = 20;
        byte[] strData;
        if (data != null && data.length > maxDataLength) {
            strData = "AAAAA".getBytes(StandardCharsets.UTF_8);
        } else {
            strData = data;
        }

        return "DefaultReplicationMessage {" +
                "id='" + id + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", type=" + type +
                ", data=" + Arrays.toString(strData) +
                ", filename='" + filename + '\'' +
                ", owner='" + owner + '\'' +
                ", fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", sequence=" + sequence +
                ", routing=" + routing +
                ", replicationStatus=" + replicationStatus +
                ", command=" + command +
                '}';
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private String id;
        private String correlationId;
        private MessageType type;

        private byte[] data;
        private String filename;
        private String owner;

        private String fromId;
        private String toId;
        private long sequence;
        private MessageRouting routing;
        private ReplicationStatus replicationStatus;
        private Command command;


        public Builder(@JsonProperty("id") String id) {
            if (id == null) {
                this.id = UUID.randomUUID().toString();
            } else {
                this.id = id;
            }
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

        public Builder sequence(long sequence) {
            this.sequence = sequence;
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

        public Builder command(Command command) {
            this.command = command;
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
