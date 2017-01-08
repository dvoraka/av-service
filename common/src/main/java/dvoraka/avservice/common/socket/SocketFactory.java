package dvoraka.avservice.common.socket;

import java.net.Socket;

/**
 * Socket factory interface.
 */
public interface SocketFactory {

    Socket createSocket(String host, int port);
}
