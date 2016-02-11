package dvoraka.avservice;

/**
 * Listening strategy for servers.
 */
public interface ListeningStrategy {

    void listen();

    void stop();
}
