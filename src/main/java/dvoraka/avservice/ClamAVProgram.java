package dvoraka.avservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * ClamAV wrapper.
 */
public class ClamAVProgram implements AVProgram {

    private static final Logger log = LogManager.getLogger(ClamAVProgram.class.getName());

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3310;

    private String socketHost;
    private int socketPort;


    public static void main(String[] args) throws IOException {

        ClamAVProgram prog = new ClamAVProgram();
        System.out.println("Connection test");
        System.out.println(prog.testConnection());
        System.out.println("Stats");
        System.out.println(prog.stats());
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

    public String stats() {

        String stats = null;
        try (
                Socket socket = new Socket(socketHost, socketPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                InputStreamReader inReader = new InputStreamReader(socket.getInputStream());
                BufferedReader in = new BufferedReader(inReader)
        ) {
            out.println("nSTATS");
            out.flush();
            stats = in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stats;
    }

    /**
     * Tests connection.
     *
     * @return the result
     */
    public boolean testConnection() {
        boolean success = false;
        Socket socket = null;
        try {
            socket = new Socket(socketHost, socketPort);
            success = true;
        } catch (UnknownHostException e) {
            log.warn("Unknown host.", e);
        } catch (IOException e) {
            log.warn("Cannot connect to the socket.", e);
        } catch (SecurityException e) {
            log.warn("Security problem.", e);
        } catch (IllegalArgumentException e) {
            log.warn("Illegal socket parameters.", e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.warn("Error while closing socket.", e);
                }
            }
        }

        return (success && ping());
    }
}
