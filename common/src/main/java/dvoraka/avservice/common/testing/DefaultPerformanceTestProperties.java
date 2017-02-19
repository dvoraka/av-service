package dvoraka.avservice.common.testing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Map;
import java.util.Optional;

/**
 * Default performance test properties implementation.
 */
public class DefaultPerformanceTestProperties implements PerformanceTestProperties {

    private static final Logger log =
            LogManager.getLogger(DefaultPerformanceTestProperties.class);

    private static final String CONF_FILE_NAME = "/loadTest.xml";

    private String host;
    private String virtualHost;
    private String appId;
    private String destinationQueue;
    private int msgCount;
    private boolean synchronous;
    private boolean sendOnly;


    public DefaultPerformanceTestProperties() {
        this(CONF_FILE_NAME);
    }

    public DefaultPerformanceTestProperties(String confName) {
        this(new Builder());

        loadPropertiesFromXml(confName);
    }

    private DefaultPerformanceTestProperties(Builder builder) {
        this.host = builder.host;
        this.virtualHost = builder.virtualHost;
        this.appId = builder.appId;
        this.destinationQueue = builder.destinationQueue;
        this.msgCount = builder.msgCount;
        this.synchronous = builder.synchronous;
        this.sendOnly = builder.sendOnly;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String vhost) {
        this.virtualHost = vhost;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDestinationQueue() {
        return destinationQueue;
    }

    private void setDestinationQueue(String queue) {
        destinationQueue = queue;
    }

    @Override
    public int getMsgCount() {
        return msgCount;
    }

    private void setMsgCount(int count) {
        msgCount = count;
    }

    public boolean isSynchronous() {
        return synchronous;
    }

    private void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    @Override
    public boolean isSendOnly() {
        return sendOnly;
    }

    private void setSendOnly(boolean sendOnly) {
        this.sendOnly = sendOnly;
    }

    @Override
    public void loadPropertiesFromXml(String filename) {
        log.info("Loading properties from XML...");
        PerformanceTestConfigParser parser = new PerformanceTestConfigParser();
        parser.parseFileSax(Optional
                .ofNullable(getClass().getResource(filename))
                .map(URL::toString)
                .orElse("/loadTest.xml"));

        Map<String, String> properties = parser.getProperties();
        loadProperties(properties);
        log.info("Properties loaded.");
    }

    /**
     * Loads properties from the map.
     *
     * @param props the properties
     */
    private void loadProperties(Map<String, String> props) {
        if (props.containsKey("host")) {
            setHost(props.get("host"));
        }
        if (props.containsKey("virtualHost")) {
            setVirtualHost(props.get("virtualHost"));
        }
        if (props.containsKey("appId")) {
            setAppId(props.get("appId"));
        }
        if (props.containsKey("destinationQueue")) {
            setDestinationQueue(props.get("destinationQueue"));
        }
        if (props.containsKey("messageCount")) {
            setMsgCount(Integer.parseInt(props.get("messageCount")));
        }
        if (props.containsKey("synchronous")) {
            setSynchronous(Boolean.parseBoolean(props.get("synchronous")));
        }
        if (props.containsKey("sendOnly")) {
            setSendOnly(Boolean.parseBoolean(props.get("sendOnly")));
        }
    }

    @Override
    public String toString() {
        return "BasicLoadTestProperties{"
                + "host='" + host + '\''
                + ", virtualHost='" + virtualHost + '\''
                + ", appId='" + appId + '\''
                + ", destinationQueue='" + destinationQueue + '\''
                + ", msgCount=" + msgCount
                + ", synchronous=" + synchronous
                + ", sendOnly=" + sendOnly
                + '}';
    }

    /**
     * Properties builder.
     */
    public static class Builder {

        private String host;
        private String virtualHost;
        private String appId;
        private String destinationQueue;
        private int msgCount;
        private boolean synchronous;
        private boolean sendOnly;

        public Builder() {
            this.host = "localhost";
            this.virtualHost = "/";
            this.appId = "";
            this.destinationQueue = "";
            this.msgCount = 1;
            this.synchronous = false;
            this.sendOnly = false;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder virtualHost(String virtualHost) {
            this.virtualHost = virtualHost;
            return this;
        }

        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder destinationQueue(String destinationQueue) {
            this.destinationQueue = destinationQueue;
            return this;
        }

        public Builder msgCount(int msgCount) {
            this.msgCount = msgCount;
            return this;
        }

        public Builder synchronous(boolean synchronous) {
            this.synchronous = synchronous;
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
