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

    private String socketHost = "localhost";
    private int socketPort = 3310;


    public static void main(String[] args) throws IOException {

        ClamAVProgram prog = new ClamAVProgram();
        System.out.println("Clamav ping test");
        System.out.println("Result: " + prog.ping());
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

        return ("PONG".equals(response));
    }
}
