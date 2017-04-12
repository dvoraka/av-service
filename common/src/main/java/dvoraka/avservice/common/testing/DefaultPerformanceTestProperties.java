package dvoraka.avservice.common.testing;

/**
 * Default performance test properties implementation.
 */
public final class DefaultPerformanceTestProperties implements PerformanceTestProperties {

    private final long msgCount;
    private final boolean sendOnly;


    private DefaultPerformanceTestProperties(Builder builder) {
        this.msgCount = builder.msgCount;
        this.sendOnly = builder.sendOnly;
    }

    @Override
    public long getMsgCount() {
        return msgCount;
    }

    @Override
    public boolean isSendOnly() {
        return sendOnly;
    }

    @Override
    public long getMaxRate() {
        return 0;
    }

    @Override
    public String toString() {
        return "DefaultPerformanceTestProperties{"
                + ", msgCount=" + msgCount
                + ", sendOnly=" + sendOnly
                + '}';
    }

    /**
     * Properties builder.
     */
    public static class Builder {

        private long msgCount;
        private boolean sendOnly;


        public Builder() {
            this.msgCount = 1;
            this.sendOnly = false;
        }

        public Builder msgCount(long msgCount) {
            this.msgCount = msgCount;
            return this;
        }

        public Builder sendOnly(boolean sendOnly) {
            this.sendOnly = sendOnly;
            return this;
        }

        public DefaultPerformanceTestProperties build() {
            return new DefaultPerformanceTestProperties(this);
        }
    }
}
