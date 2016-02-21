package dvoraka.avservice.data;

import java.util.UUID;

/**
 * Default AV message implementation.
 */
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
        return new Builder(null)
                .correlationId(this.getId())
                .virusInfo(virus + "")
                .type(AVMessageType.RESPONSE)
                .build();
    }

    public static class Builder {
        private String id;
        private String correlationId;
        private byte[] data;
        private AVMessageType type;
        private String serviceId;
        private String virusInfo;

        public Builder(String id) {
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
