package dvoraka.avservice.avprogram;

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
    private static final String CLEAN_STREAM_RESPONSE = "stream: OK";
    private static final int CHUNK_LENGTH_BYTE_SIZE = 4;

    private String socketHost;
    private int socketPort;


    public static void main(String[] args) throws IOException {

        ClamAVProgram program = new ClamAVProgram();
        System.out.println("Connection test");
        System.out.println(program.testConnection());
        System.out.println("Stats");
        System.out.println(program.stats());
        System.out.println("Clamav ping test");
        System.out.println("Result: " + program.ping());
        System.out.println("Clamav version");
        System.out.println("Result: " + program.version());
        System.out.println("Clamav checking data");
        System.out.println("Result: " + program.scanStream("aaa".getBytes()));
        System.out.println("Clamav checking data");
        System.out.println("Result: " + program.scanStream(
                "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*"
                        .getBytes()));
    }

    public ClamAVProgram() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public ClamAVProgram(String socketHost, int socketPort) {
        this.socketHost = socketHost;
        this.socketPort = socketPort;
    }

    /**
     * Scans the stream of bytes.
     *
     * @param bytes the bytes to scan
     * @return infection found
     * <p/>
     * <p>
     * <b>Clamd documentation:</b>
     * </p>
     * INSTREAM
     * <br>
     * It is mandatory to prefix this command with n or z.
     * Scan a stream of data. The stream is sent to  clamd  in  chunks,
     * after  INSTREAM,  on  the  same  socket on which the command was
     * sent.  This avoids the overhead of establishing new TCP  connec‐
     * tions  and  problems  with  NAT.  The  format  of  the chunk is:
     * '<length><data>' where <length> is the  size  of  the  following
     * data  in bytes expressed as a 4 byte unsigned integer in network
     * byte order and <data> is the actual chunk. Streaming  is  termi‐
     * nated  by  sending  a  zero-length  chunk.  Note:  do not exceed
     * StreamMaxLength as defined in clamd.conf, otherwise  clamd  will
     * reply  with  INSTREAM  size limit exceeded and close the connec‐
     * tion.
     */
    @Override
    public boolean scanStream(byte[] bytes) {
        log.debug("Scanning stream...");

        try (
                Socket socket = new Socket(socketHost, socketPort);
                OutputStream outStream = socket.getOutputStream();
                InputStreamReader inReader = new InputStreamReader(socket.getInputStream());
                BufferedReader in = new BufferedReader(inReader)
        ) {
            // send bytes
            byte[] lengthBytes = ByteBuffer.allocate(CHUNK_LENGTH_BYTE_SIZE)
                    .order(ByteOrder.BIG_ENDIAN).putInt(bytes.length).array();
            outStream.write("nINSTREAM\n".getBytes("UTF-8"));
            outStream.write(lengthBytes);
            outStream.write(bytes);

            // terminate stream with zero length chunk
            byte[] zeroLengthBytes = ByteBuffer.allocate(CHUNK_LENGTH_BYTE_SIZE)
                    .order(ByteOrder.BIG_ENDIAN).putInt(0).array();
            outStream.write(zeroLengthBytes);
            outStream.flush();

            // read check result
            String response = in.readLine();

            log.debug("scanning done.");
            if (response.equals(CLEAN_STREAM_RESPONSE)) {
                return false;
            } else {
                log.debug("Virus found: " + response);
                return true;
            }

        } catch (IOException e) {
            log.warn("Scanning problem!", e);
        }

        // TODO: throw exception
        // in case of any error return true
        return true;
    }

    @Override
    public String scanStreamWithInfo(byte[] bytes) {
        // TODO: implement
        return "info";
    }

    @Override
    public boolean isRunning() {
        return testConnection();
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
        log.debug("Testing connection...");
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
