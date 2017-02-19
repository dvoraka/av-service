package dvoraka.avservice.common.testing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Default performance test properties implementation.
 */
public final class DefaultPerformanceTestProperties implements PerformanceTestProperties {

    private static final Logger log = LogManager.getLogger(DefaultPerformanceTestProperties.class);

    private long msgCount;
    private boolean sendOnly;


    private DefaultPerformanceTestProperties(Builder builder) {
        this.msgCount = builder.msgCount;
        this.sendOnly = builder.sendOnly;
    }

    @Override
    public long getMsgCount() {
        return msgCount;
    }

    private void setMsgCount(long count) {
        msgCount = count;
    }

    @Override
    public boolean isSendOnly() {
        return sendOnly;
    }

    private void setSendOnly(boolean sendOnly) {
        this.sendOnly = sendOnly;
    }

    /**
     * Loads properties from the map.
     *
     * @param props the properties
     */
    private void loadProperties(Map<String, String> props) {
        if (props.containsKey("messageCount")) {
            setMsgCount(Integer.parseInt(props.get("messageCount")));
        }
        if (props.containsKey("sendOnly")) {
            setSendOnly(Boolean.parseBoolean(props.get("sendOnly")));
        }
    }

    @Override
    public String toString() {
        return "BasicLoadTestProperties{"
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
