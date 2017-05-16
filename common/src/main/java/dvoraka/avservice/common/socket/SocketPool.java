package dvoraka.avservice.common.socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Socket pool prototype.
 * <p>
 * Current class is a mix of ClamAvSocketPool and abstract BaseSocketPool.
 */
public class SocketPool implements SocketFactory {

    private static final Logger log = LogManager.getLogger(SocketPool.class);

    private final String host;
    private final int port;
    private final SocketFactory socketFactory;

    private final List<SocketWrapper> socketWrappers;
    private final BlockingQueue<SocketWrapper> availableSocketWrappers;


    public SocketPool(int socketCount, String host, int port, SocketFactory factory) {
        this.host = host;
        this.port = port;

        if (factory == null) {
            socketFactory = this;
        } else {
            socketFactory = factory;
        }

        socketWrappers = Stream
                .generate(() -> new SocketWrapper(host, port, socketFactory))
                .limit(socketCount)
                .collect(Collectors.toList());

        availableSocketWrappers = new ArrayBlockingQueue<>(socketCount);
        availableSocketWrappers.addAll(socketWrappers);
    }

    public void close() {
        log.info("Closing sockets...");
        socketWrappers.forEach(SocketWrapper::releaseSocket);
        log.info("Done.");
    }

    /**
     * Returns an available socket.
     *
     * @return the socket
     */
    public SocketWrapper getSocket() {
        return availableSocketWrappers.poll();
    }

    /**
     * Returns an used socket.
     *
     * @param socket the used socket
     */
    public void returnSocket(SocketWrapper socket) {
        availableSocketWrappers.add(socket);
    }

    public int availableSockets() {
        return availableSocketWrappers.size();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public SocketFactory getSocketFactory() {
        return socketFactory;
    }

    public int getSize() {
        return socketWrappers.size();
    }

    @Override
    public Socket createSocket(String host, int port) {
        log.debug("Creating socket, host: {}, port: {}", host, port);
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            socket.setTcpNoDelay(true);
        } catch (IOException e) {
            log.error("Socket creating error!", e);
        }

        return socket;
    }

    public static class SocketWrapper {

        private final String host;
        private final int port;
        private final SocketFactory socketFactory;

        private Socket socket;
        private OutputStream outputStream;
        private BufferedReader reader;


        SocketWrapper(String host, int port, SocketFactory socketFactory) {
            this.host = requireNonNull(host);
            this.port = port;
            this.socketFactory = requireNonNull(socketFactory);
        }

        private void initialize() {
            if (socket == null) {
                log.debug("Initializing socket...");

                outputStream = null;
                reader = null;

                try {
                    socket = socketFactory.createSocket(host, port);

                    outputStream = socket.getOutputStream();
                    outputStream.write("nIDSESSION\n".getBytes("UTF-8"));

                    reader = new BufferedReader(new InputStreamReader(
                            socket.getInputStream(), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    log.error("Socket problem!", e);
                    socket = null;
                }
            }
        }

        public void fix() {
            log.info("Fixing socket...");
            if (socket != null) {
                log.debug("Closing old socket...");
                try {
                    outputStream.close();
                    reader.close();
                    socket.close();
                } catch (IOException e) {
                    log.warn("Socket closing failed!", e);
                }
            }
            socket = null;
            initialize();
        }

        public OutputStream getOutputStream() {
            initialize();

            return outputStream;
        }

        public BufferedReader getBufferedReader() {
            initialize();

            return reader;
        }

        private void releaseSocket() {
            log.info("Releasing socket...");

            if (socket == null) {
                return;
            }

            try {
                OutputStream out = socket.getOutputStream();
                out.write("nEND\n".getBytes("UTF-8"));

                getBufferedReader().close();
                getOutputStream().close();
                socket.close();
            } catch (IOException e) {
                log.warn("Release socket problem!", e);
            }
        }
    }
}
