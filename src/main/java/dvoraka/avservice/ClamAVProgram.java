package dvoraka.avservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ClamAV wrapper.
 */
public class ClamAVProgram implements AVProgram {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3310;

    private String socketHost;
    private int socketPort;


    public static void main(String[] args) throws IOException {

        ClamAVProgram prog = new ClamAVProgram();
        System.out.println("Clamav ping test");
        System.out.println("Result: " + prog.ping());
    }

    public ClamAVProgram() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public ClamAVProgram(String socketHost, int socketPort) {
        this.socketHost = socketHost;
        this.socketPort = socketPort;
    }

    @Override
    public boolean scanStream(byte[] bytes) {
        return false;
    }

    public boolean ping() {

        String response = null;
        try (
                Socket socket = new Socket(socketHost, socketPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                InputStreamReader inReader = new InputStreamReader(socket.getInputStream());
                BufferedReader in = new BufferedReader(inReader)
        ) {
            out.println("nPING");
            out.flush();
            response = in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "PONG".equals(response);
    }
}
