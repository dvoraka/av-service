package dvoraka.avservice.checker;

import dvoraka.avservice.checker.utils.LoadTestConfigParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Basic load test properties.
 * <p>
 * Created by dvoraka on 18.4.14.
 */
public class BasicProperties implements LoadTestProperties {

    private static Logger logger = LogManager.getLogger();

    private String host;
    private String virtualHost;
    private String appId;
    private String destinationQueue;
    private int msgCount;
    private boolean synchronous;
    private boolean sendOnly;


    public BasicProperties() {
        // default values
        this.host = "localhost";
        this.virtualHost = "/";
        this.appId = "";
        this.destinationQueue = "";
        this.msgCount = 1;
        this.synchronous = false;
        this.sendOnly = false;

        try {
            loadPropertiesFromXML();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BasicProperties(Builder builder) {
        this.host = builder.host;
        this.virtualHost = builder.virtualHost;
        this.appId = builder.appId;
        this.destinationQueue = builder.destinationQueue;
        this.msgCount = builder.msgCount;
        this.synchronous = builder.synchronous;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String getVirtualHost() {
        return virtualHost;
    }

    @Override
    public void setVirtualHost(String vhost) {
        this.virtualHost = vhost;
    }

    @Override
    public String getAppId() {
        return appId;
    }

    @Override
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    public String getDestinationQueue() {
        return destinationQueue;
    }

    @Override
    public void setDestinationQueue(String queue) {
        destinationQueue = queue;
    }

    @Override
    public int getMsgCount() {
        return msgCount;
    }

    @Override
    public void setMsgCount(int count) {
        msgCount = count;
    }

    @Override
    public boolean isSynchronous() {
        return synchronous;
    }

    @Override
    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    @Override
    public boolean isSendOnly() {
        return sendOnly;
    }

    @Override
    public void setSendOnly(boolean sendOnly) {
        this.sendOnly = sendOnly;
    }

    @Override
    public void loadPropertiesFromXML() throws IOException {
        LoadTestConfigParser parser = new LoadTestConfigParser();
        parser.parseFileSax(getClass().getResource("/loadTest.xml").toString());

        Map<String, String> properties = parser.getProperties();
        loadProperties(properties);
    }

    /**
     * Loads properties from map
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

    public static class Builder {

        private String host;
        private String virtualHost;
        private String appId;
        private String destinationQueue;
        private int msgCount;
        private boolean synchronous;

        public Builder() {
            this.host = "localhost";
            this.virtualHost = "/";
            this.appId = "";
            this.destinationQueue = "";
            this.msgCount = 1;
            this.synchronous = false;
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

        public BasicProperties build() {
            return new BasicProperties(this);
        }
    }
}
