package dvoraka.avservice.common;

/**
 * Interface for load test properties.
 */
public interface LoadTestProperties extends TestProperties {

    String getHost();

    void setHost(String host);

    String getVirtualHost();

    void setVirtualHost(String vhost);

    String getAppId();

    void setAppId(String appId);

    String getDestinationQueue();

    void setDestinationQueue(String queue);

    int getMsgCount();

    void setMsgCount(int count);

    boolean isSynchronous();

    void setSynchronous(boolean synchronous);

    boolean isSendOnly();

    void setSendOnly(boolean sendOnly);
}
