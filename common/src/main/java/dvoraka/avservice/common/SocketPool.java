package dvoraka.avservice.common;

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

/**
 * Socket pool prototype.
 *
 * Current class is a mix of ClamAvSocketPool and abstract BaseSocketPool.
 */
public class SocketPool {

    private static final Logger log = LogManager.getLogger(SocketPool.class);

    private String host;
    private int port;

    private List<SocketWrapper> socketWrappers;
    private BlockingQueue<SocketWrapper> availableSocketWrappers;


    // TODO: add connection factory
    public SocketPool(int socketCount, String host, int port) {
        this.host = host;
        this.port = port;

        socketWrappers = Stream
                .generate(() -> new SocketWrapper(host, port))
                .limit(socketCount)
                .collect(Collectors.toList());

        availableSocketWrappers = new ArrayBlockingQueue<>(socketCount);
        socketWrappers.forEach(availableSocketWrappers::add);
    }

    public void close() {
        socketWrappers.forEach(SocketWrapper::releaseSocket);
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

    public static class SocketWrapper {

        private String host;
        private int port;

        private Socket socket;
        private OutputStream outputStream;
        private BufferedReader reader;


        SocketWrapper(String host, int port) {
            this.host = host;
            this.port = port;
        }

        private void initialize() {
            if (socket == null) {
                try {
                    socket = createSocket(host, port);

                    OutputStream out = socket.getOutputStream();
                    out.write("nIDSESSION\n".getBytes("UTF-8"));
                } catch (IOException e) {
                    log.error("Socket problem!", e);
                }
            } else if (socket.isClosed()) {
                socket = null;
                initialize();
            }
        }

        public OutputStream getOutputStream() {
            initialize();

            if (outputStream == null) {
                try {
                    outputStream = socket.getOutputStream();
                } catch (IOException e) {
                    log.error("OutputStream error!", e);
                }
            }

            return outputStream;
        }

        public BufferedReader getBufferedReader() {
            initialize();

            if (reader == null) {
                try {
                    reader = new BufferedReader(new InputStreamReader(
                            socket.getInputStream(), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    log.error("Reader problem!", e);
                }
            }

            return reader;
        }

        public void releaseSocket() {
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
                e.printStackTrace();
            }
        }

        private Socket createSocket(String host, int port) {
            Socket s = null;
            try {
                s = new Socket(host, port);
                s.setTcpNoDelay(true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return s;
        }
    }
}
