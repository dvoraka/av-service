package dvoraka.avservice.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import dvoraka.avservice.common.Utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * Default AV message implementation.
 */
@JsonDeserialize(builder = DefaultAvMessage.Builder.class)
public final class DefaultAvMessage implements AvMessage {

    private final String id;
    private final String correlationId;
    private final byte[] data;
    private final String serviceId;
    private final String virusInfo;
    private final AvMessageType type;


    private DefaultAvMessage(Builder builder) {
        this.id = builder.id;
        this.correlationId = builder.correlationId;
        this.data = builder.data;
        this.serviceId = builder.serviceId;
        this.virusInfo = builder.virusInfo;
        this.type = builder.type;
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
    public byte[] getData() {
        if (data != null) {
            return data.clone();
        }
        return new byte[0];
    }

    @Override
    public AvMessageType getType() {
        return type;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public String getVirusInfo() {
        return virusInfo;
    }

    @Override
    public AvMessage createResponse(String virusInfo) {
        return new Builder(Utils.genUuidString())
                .correlationId(this.getId())
                .virusInfo(virusInfo)
                .type(AvMessageType.RESPONSE)
                .build();
    }

    @Override
    public AvMessage createErrorResponse(String errorMessage) {
        return new Builder(Utils.genUuidString())
                .correlationId(this.getId())
                .type(AvMessageType.RESPONSE_ERROR)
                .data(errorMessage.getBytes(StandardCharsets.UTF_8))
                .build();
    }

    /**
     * Compares the whole messages - all fields.
     *
     * @param o the other message
     * @return true if messages are completely equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultAvMessage that = (DefaultAvMessage) o;

        if (!id.equals(that.id)) {
            return false;
        }
        if (correlationId != null
                ? !correlationId.equals(that.correlationId)
                : that.correlationId != null) {
            return false;
        }
        if (!Arrays.equals(data, that.data)) {
            return false;
        }
        if (serviceId != null ? !serviceId.equals(that.serviceId) : that.serviceId != null) {
            return false;
        }
        if (virusInfo != null ? !virusInfo.equals(that.virusInfo) : that.virusInfo != null) {
            return false;
        }

        return type == that.type;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "DefaultAvMessage {"
                + "id='" + id + '\''
                + ", correlationId='" + correlationId + '\''
                + ", data=" + Arrays.toString(data)
                + ", serviceId='" + serviceId + '\''
                + ", virusInfo='" + virusInfo + '\''
                + ", type=" + type
                + '}';
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private String id;
        private String correlationId;
        private byte[] data;
        private AvMessageType type;
        private String serviceId;
        private String virusInfo;


        public Builder(@JsonProperty("id") String id) {
            this.id = Objects.requireNonNull(id, "ID must not be null!");
        }

        public Builder correlationId(String id) {
            this.correlationId = id;
            return this;
        }

        public Builder data(byte[] data) {
            if (data != null) {
                this.data = data.clone();
            }
            return this;
        }

        public Builder type(AvMessageType type) {
            this.type = type;
            return this;
        }

        public Builder serviceId(String id) {
            this.serviceId = id;
            return this;
        }

        public Builder virusInfo(String info) {
            this.virusInfo = info;
            return this;
        }

        public DefaultAvMessage build() {
            return new DefaultAvMessage(this);
        }
    }
}
