package dvoraka.avservice.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Arrays;
import java.util.UUID;

/**
 * Default AV message implementation.
 */
@JsonDeserialize(builder = DefaultAVMessage.Builder.class)
public class DefaultAVMessage implements AVMessage {

    private String id;
    private String correlationId;
    private byte[] data;
    private String serviceId;
    private String virusInfo;
    private AVMessageType type;


    private DefaultAVMessage(Builder builder) {
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
        return data;
    }

    @Override
    public AVMessageType getType() {
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
    public AVMessage createResponse(boolean virus) {
        String virusInfo = virus ? "virus info" : "";

        return new Builder(null)
                .correlationId(this.getId())
                .virusInfo(virusInfo)
                .type(AVMessageType.RESPONSE)
                .build();
    }

    @Override
    public String toString() {
        return "DefaultAVMessage {" +
                "id='" + id + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", data=" + Arrays.toString(data) +
                ", serviceId='" + serviceId + '\'' +
                ", virusInfo='" + virusInfo + '\'' +
                ", type=" + type +
                '}';
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private String id;
        private String correlationId;
        private byte[] data;
        private AVMessageType type;
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
            this.data = data;
            return this;
        }

        public Builder type(AVMessageType type) {
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

        public DefaultAVMessage build() {
            return new DefaultAVMessage(this);
        }
    }
}
