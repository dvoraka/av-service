package dvoraka.avservice.common.testing;

/**
 * Interface for performance test properties.
 */
public interface PerformanceTestProperties extends TestProperties {

    long getMsgCount();

    boolean isSendOnly();
}
