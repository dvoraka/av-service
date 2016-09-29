package dvoraka.avservice.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

/**
 * Default AV message implementation.
 */
@JsonDeserialize(builder = DefaultAvMessage.Builder.class)
public final class DefaultAvMessage implements AvMessage {

    private String id;
    private String correlationId;
    private byte[] data;
    private String serviceId;
    private String virusInfo;
    private AvMessageType type;


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
    public AvMessage createResponse(boolean virus) {
        String virusInfoMsg = virus ? "virus info" : "";

        return new Builder(null)
                .correlationId(this.getId())
                .virusInfo(virusInfoMsg)
                .type(AvMessageType.RESPONSE)
                .build();
    }

    @Override
    public AvMessage createResponseWithString(String virusInfo) {
        return new Builder(null)
                .correlationId(this.getId())
                .virusInfo(virusInfo)
                .type(AvMessageType.RESPONSE)
                .build();
    }

    @Override
    public AvMessage createErrorResponse(String errorMessage) {
        return new Builder(null)
                .correlationId(this.getId())
                .type(AvMessageType.RESPONSE_ERROR)
                .data(errorMessage.getBytes(StandardCharsets.UTF_8))
                .build();
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
            if (id != null) {
                this.id = id;
            } else {
                this.id = UUID.randomUUID().toString();
            }
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
