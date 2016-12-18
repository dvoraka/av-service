package dvoraka.avservice.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
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


    public static void main(String[] args) {
        SocketPool pool = new SocketPool(5, "localhost", 3310);
    }

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

    public SocketWrapper getSocket() {
        return availableSocketWrappers.poll();
    }

    public void returnSocket(SocketWrapper socket) {
        availableSocketWrappers.add(socket);
    }

    static class SocketWrapper {

        private String host;
        private int port;

        private Socket socket;


        SocketWrapper(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public OutputStream getOutputStream() {
            return null;
        }

        public BufferedReader getBufferedReader() {
            return null;
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
