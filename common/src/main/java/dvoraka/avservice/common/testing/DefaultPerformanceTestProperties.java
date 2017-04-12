package dvoraka.avservice.common.testing;

/**
 * Default performance test properties implementation.
 */
public final class DefaultPerformanceTestProperties implements PerformanceTestProperties {

    private final long msgCount;
    private final boolean sendOnly;
    private final long maxRate;


    private DefaultPerformanceTestProperties(Builder builder) {
        this.msgCount = builder.msgCount;
        this.sendOnly = builder.sendOnly;
        this.maxRate = builder.maxRate;
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
        return maxRate;
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
        private long maxRate;


        public Builder() {
            this.msgCount = 1;
            this.sendOnly = false;
            this.maxRate = 0;
        }

        public Builder msgCount(long msgCount) {
            this.msgCount = msgCount;
            return this;
        }

        public Builder sendOnly(boolean sendOnly) {
            this.sendOnly = sendOnly;
            return this;
        }

        public Builder maxRate(long maxRate) {
            this.maxRate = maxRate;
            return this;
        }

        public DefaultPerformanceTestProperties build() {
            return new DefaultPerformanceTestProperties(this);
        }
    }
}
