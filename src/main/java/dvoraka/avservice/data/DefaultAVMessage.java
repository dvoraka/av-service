package dvoraka.avservice.data;

import dvoraka.avservice.AVMessageType;

/**
 * Default AV message implementation.
 */
public class DefaultAVMessage implements AVMessage {

    private byte[] data;
    private String serviceId;
    private String virusInfo;
    private AVMessageType type;


    private DefaultAVMessage(Builder builder) {
        this.data = builder.data;
        this.serviceId = builder.serviceId;
        this.virusInfo = builder.virusInfo;
        this.type = builder.type;
    }

    public DefaultAVMessage(byte[] data, AVMessageType type) {
        this.data = data;
        this.type = type;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getCorrelationId() {
        return null;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public AVMessageType getType() {
        return null;
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
        return new Builder("dfdfdfd")
                .correlationId(this.getId())
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
