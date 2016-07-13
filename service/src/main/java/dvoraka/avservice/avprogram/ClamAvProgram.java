package dvoraka.avservice.avprogram;

import dvoraka.avservice.exception.ScanErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * ClamAV wrapper.
 */
public class ClamAvProgram implements AvProgram {

    private static final Logger log = LogManager.getLogger(ClamAvProgram.class.getName());

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3310;
    private static final String CLEAN_STREAM_RESPONSE = "stream: OK";
    private static final int CHUNK_LENGTH_BYTE_SIZE = 4;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private String socketHost;
    private int socketPort;


    public ClamAvProgram() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public ClamAvProgram(String socketHost, int socketPort) {
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
    public boolean scanStream(byte[] bytes) throws ScanErrorException {
        String response;
        try {
            response = scanStreamWithInfo(bytes);
        } catch (ScanErrorException e) {
            log.warn("Scanning failed!", e);
            throw new ScanErrorException("Scanning failed!", e);
        }

        if (response.equals(CLEAN_STREAM_RESPONSE)) {
            return false;
        } else {
            log.debug("Virus found: " + response);
            return true;
        }
    }

    @Override
    public String scanStreamWithInfo(byte[] bytes) throws ScanErrorException {
        log.debug("Scanning stream...");
        try (
                Socket socket = new Socket(socketHost, socketPort);
                OutputStream outStream = socket.getOutputStream();
                InputStreamReader inReader = new InputStreamReader(socket.getInputStream(), DEFAULT_CHARSET);
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
            if (response != null) {
                return response;
            } else {
                log.warn("Response reading problem!");
                throw new ScanErrorException("Scanning problem.");
            }
        } catch (IOException e) {
            log.warn("Scanning problem!", e);
            throw new ScanErrorException("Scanning problem.", e);
        }
    }

    @Override
    public boolean isRunning() {
        return testConnection();
    }

    private String command(String command) {
        String result = null;
        try (
                Socket socket = new Socket(socketHost, socketPort);
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), DEFAULT_CHARSET));
                InputStreamReader inReader = new InputStreamReader(socket.getInputStream(), DEFAULT_CHARSET);
                BufferedReader in = new BufferedReader(inReader)
        ) {
            out.println("n" + command);
            out.flush();
            result = in.readLine();

        } catch (IOException e) {
            log.warn("ClamAV problem!", e);
        }

        return result;
    }

    public boolean ping() {
        return "PONG".equals(command("PING"));
    }

    public String version() {
        return command("VERSION");
    }

    public String stats() {
        return command("STATS");
    }

    protected Socket createSocket() throws IOException {
        return new Socket(socketHost, socketPort);
    }

    /**
     * Tests connection.
     *
     * @return the result
     */
    public boolean testConnection() {
        log.debug("Testing connection...");

        boolean success = false;
        try (Socket ignored = createSocket()) {
            success = true;
        } catch (UnknownHostException e) {
            log.warn("Unknown host.", e);
        } catch (IOException e) {
            log.warn("Cannot connect to the socket.", e);
        } catch (SecurityException e) {
            log.warn("Security problem.", e);
        } catch (IllegalArgumentException e) {
            log.warn("Illegal socket parameters.", e);
        }

        return success && ping();
    }
}
