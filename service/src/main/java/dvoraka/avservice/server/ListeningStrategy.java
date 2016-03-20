package dvoraka.avservice.server;

/**
 * Listening strategy for servers.
 */
public interface ListeningStrategy {

    void listen();

    void stop();
}
