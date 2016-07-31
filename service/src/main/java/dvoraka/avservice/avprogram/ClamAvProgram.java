package dvoraka.avservice.avprogram;

import dvoraka.avservice.common.exception.ScanErrorException;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * ClamAV wrapper.
 */
public class ClamAvProgram implements AvProgram {

    private static final Logger log = LogManager.getLogger(ClamAvProgram.class.getName());

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3310;
    private static final boolean DEFAULT_CACHING = false;
    public static final String CLEAN_STREAM_RESPONSE = "stream: OK";
    private static final int CHUNK_LENGTH_BYTE_SIZE = 4;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private String socketHost;
    private int socketPort;

    private volatile boolean caching;
    private ConcurrentMap<String, String> scanCache;
    private Base64.Encoder b64encoder;
    private MessageDigest digest;


    public ClamAvProgram() {
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_CACHING);
    }

    public ClamAvProgram(String socketHost, int socketPort, boolean caching) {
        this.socketHost = socketHost;
        this.socketPort = socketPort;

        this.caching = caching;
        if (this.caching) {
            initCaching();
        }
    }

    private synchronized void initCaching() {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.warn("Algorithm not found!", e);
        }

        if (digest != null) {
            scanCache = new ConcurrentHashMap<>();
            b64encoder = Base64.getEncoder();

            caching = true;
        } else {
            caching = false;
        }
    }

    private synchronized void disableCaching() {
        caching = false;
        // TODO: clean cache
    }

    @Override
    public boolean scanBytes(byte[] bytes) throws ScanErrorException {
        String response;
        try {
            response = scanBytesWithInfo(bytes);
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

    /**
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
    public String scanBytesWithInfo(byte[] bytes) throws ScanErrorException {
        if (caching) {
            String arrayValue = arrayHash(bytes);
            if (scanCache.containsKey(arrayValue)) {
                return scanCache.get(arrayValue);
            }
        }

        log.debug("Scanning stream...");
        try (
                Socket socket = createSocket();
                OutputStream outStream = socket.getOutputStream();
                InputStreamReader inReader = new InputStreamReader(socket.getInputStream(), DEFAULT_CHARSET);
                BufferedReader in = new BufferedReader(inReader)
        ) {
            // send bytes
            outStream.write("nINSTREAM\n".getBytes("UTF-8"));
            outStream.write(intBytes(bytes.length, CHUNK_LENGTH_BYTE_SIZE));
            outStream.write(bytes);

            // terminate stream with a zero length chunk
            outStream.write(intBytes(0, CHUNK_LENGTH_BYTE_SIZE));
            outStream.flush();

            // read check result
            String response = in.readLine();

            log.debug("scanning done.");
            if (response != null) {
                if (caching) {
                    addToCache(bytes, response);
                }

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

    private byte[] intBytes(int number, int size) {
        return ByteBuffer
                .allocate(size)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(number)
                .array();
    }

    private synchronized String arrayHash(byte[] bytes) {
        return b64encoder.encodeToString(digest.digest(bytes));
    }

    private void addToCache(byte[] bytes, String response) {
        // TODO: create arrayHash in a new thread and then add the hash into the cache
        scanCache.put(arrayHash(bytes), response);
    }

    @Override
    public boolean isRunning() {
        return testConnection();
    }

    @Override
    public void setCaching(boolean caching) {
        if (caching) {
            initCaching();
        } else {
            disableCaching();
        }
    }

    @Override
    public boolean isCaching() {
        return caching;
    }

    @Override
    public long getMaxSize() {
        return 0;
    }

    private String command(String command) {
        String result = null;
        try (
                Socket socket = createSocket();
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
