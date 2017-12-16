package dvoraka.avservice.common.testing;

/**
 * Default performance test properties implementation.
 */
public final class DefaultPerformanceTestProperties implements PerformanceTestProperties {

    private final long msgCount;
    private final boolean sendOnly;
    private final long maxRate;
    private final int threadCount;


    private DefaultPerformanceTestProperties(Builder builder) {
        this.msgCount = builder.msgCount;
        this.sendOnly = builder.sendOnly;
        this.maxRate = builder.maxRate;
        this.threadCount = builder.threadCount;
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
    public int getThreadCount() {
        return threadCount;
    }

    @Override
    public String toString() {
        return "DefaultPerformanceTestProperties{" +
                "msgCount=" + msgCount +
                ", sendOnly=" + sendOnly +
                ", maxRate=" + maxRate +
                ", threadCount=" + threadCount +
                '}';
    }

    /**
     * Properties builder.
     */
    public static class Builder {

        private long msgCount;
        private boolean sendOnly;
        private long maxRate;
        private int threadCount;


        public Builder() {
            this.msgCount = 1;
            this.sendOnly = false;
            this.maxRate = 0;
            this.threadCount = 1;
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

        public Builder threadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        public DefaultPerformanceTestProperties build() {
            return new DefaultPerformanceTestProperties(this);
        }
    }
}
