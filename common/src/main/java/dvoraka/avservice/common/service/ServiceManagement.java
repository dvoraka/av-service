package dvoraka.avservice.common.service;

/**
 * Management for services.
 */
public interface ServiceManagement {

    void start();

    void stop();

    void restart();

    boolean isRunning();
}
