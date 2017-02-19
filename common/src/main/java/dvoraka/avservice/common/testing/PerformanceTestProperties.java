package dvoraka.avservice.common.testing;

/**
 * Interface for performance test properties.
 */
public interface PerformanceTestProperties extends TestProperties {

    String getHost();

    String getVirtualHost();

    String getAppId();

    String getDestinationQueue();

    int getMsgCount();

    boolean isSynchronous();

    boolean isSendOnly();
}
