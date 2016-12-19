package dvoraka.avservice.common;

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
 */
public class SocketPool {

    private String host;
    private int port;

    private List<SocketWrapper> socketWrappers;
    private BlockingQueue<SocketWrapper> availableSocketWrappers;


    public SocketPool(int socketCount, String host, int port) {
        this.host = host;
        this.port = port;

        socketWrappers = Stream
                .generate(() -> new SocketWrapper(host, port))
                .limit(socketCount)
                .collect(Collectors.toList());

        availableSocketWrappers = new ArrayBlockingQueue<>(socketCount);
        socketWrappers
                .forEach(availableSocketWrappers::add);
    }

    /**
     * Returns an available socket.
     *
     * @return the socket
     */
    public SocketWrapper getSocket() {
//        return availableSocketWrappers.poll();
        return socketWrappers.get(0);
    }

    /**
     * Returns an used socket.
     *
     * @param socket the used socket
     */
    public void returnSocket(SocketWrapper socket) {
        availableSocketWrappers.add(socket);
    }

    public class SocketWrapper {

        private String host;
        private int port;

        private Socket socket;


        SocketWrapper(String host, int port) {
            this.host = host;
            this.port = port;
        }

        private void initialize() {
            if (socket == null) {
                try {
                    socket = new Socket(host, port);
                    OutputStream out = socket.getOutputStream();
                    out.write("nIDSESSION\n".getBytes("UTF-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (socket.isClosed()) {
                socket = null;
                initialize();
            }
        }

        public OutputStream getOutputStream() {
            initialize();

            OutputStream outStream = null;
            try {
                outStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return outStream;
        }

        public BufferedReader getBufferedReader() {
            initialize();

            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(
                                socket.getInputStream(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return reader;
        }

        public void releaseSocket() {

        }

        private Socket createSocket() {
            Socket s = null;
            try {
                s = new Socket(host, port);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return s;
        }
    }
}
