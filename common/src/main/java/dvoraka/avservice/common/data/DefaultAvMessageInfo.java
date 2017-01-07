package dvoraka.avservice.common.data;

import java.time.Instant;

/**
 * Default AV message info implementation.
 */
public final class DefaultAvMessageInfo implements AvMessageInfo {

    private final String id;
    private final AvMessageSource source;
    private final String serviceId;
    private final Instant created;


    private DefaultAvMessageInfo(Builder builder) {
        this.id = builder.id;
        this.source = builder.source;
        this.serviceId = builder.serviceId;
        this.created = builder.created;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public AvMessageSource getSource() {
        return source;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public Instant getCreated() {
        return created;
    }

    public static class Builder {

        private String id;
        private AvMessageSource source;
        private String serviceId;
        private Instant created;


        public Builder(String id) {
            this.id = id;
        }

        public Builder source(AvMessageSource source) {
            this.source = source;
            return this;
        }

        public Builder serviceId(String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public Builder created(Instant created) {
            this.created = created;
            return this;
        }

        public DefaultAvMessageInfo build() {
            return new DefaultAvMessageInfo(this);
        }
    }
}
