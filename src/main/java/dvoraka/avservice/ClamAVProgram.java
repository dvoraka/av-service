package dvoraka.avservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
        System.out.println("Clamav version");
        System.out.println("Result: " + prog.version());
        System.out.println("Clamav checking data");
        System.out.println("Result: " + prog.scanStream("aaa".getBytes()));
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

        String response = null;
        try (
                Socket socket = new Socket(socketHost, socketPort);
                OutputStream outStream = socket.getOutputStream();
                InputStreamReader inReader = new InputStreamReader(socket.getInputStream());
                BufferedReader in = new BufferedReader(inReader)
        ) {
            byte[] lenghtBytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(bytes.length).array();
            outStream.write("nINSTREAM\n".getBytes("UTF-8"));
            outStream.write(lenghtBytes);
            outStream.write(bytes);

            byte[] zeroLengthBytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(0).array();
            outStream.write(zeroLengthBytes);
            outStream.flush();

            response = in.readLine();
            System.out.println(response);

        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public String version() {

        String version = null;
        try (
                Socket socket = new Socket(socketHost, socketPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                InputStreamReader inReader = new InputStreamReader(socket.getInputStream());
                BufferedReader in = new BufferedReader(inReader)
        ) {
            out.println("nVERSION");
            out.flush();
            version = in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return version;
    }
}
