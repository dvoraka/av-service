package dvoraka.avservice.common.socket;

import java.net.Socket;

/**
 * Socket factory interface.
 */
@FunctionalInterface
public interface SocketFactory {

    /**
     * Creates a socket with a given host and port.
     *
     * @param host the host
     * @param port the port
     * @return the new socket
     */
    Socket createSocket(String host, int port);
}
