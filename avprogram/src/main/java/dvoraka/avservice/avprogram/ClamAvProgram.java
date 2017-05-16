package dvoraka.avservice.avprogram;

import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.exception.ScanException;
import dvoraka.avservice.common.service.CachingService;
import dvoraka.avservice.common.socket.SocketPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * ClamAV wrapper. Uses network sockets.
 */
@Component
public class ClamAvProgram implements AvProgram {

    private static final Logger log = LogManager.getLogger(ClamAvProgram.class);

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 3310;
    public static final int DEFAULT_MAX_ARRAY_SIZE = 10_000;

    private static final String ERROR_MSG = "Scanning problem!";

    public static final String CLEAN_STREAM_RESPONSE = Utils.OK_VIRUS_INFO;
    private static final int CHUNK_LENGTH_BYTE_SIZE = 4;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private CachingService cachingService;
    private volatile boolean caching;

    private final String socketHost;
    private final int socketPort;
    private final long maxArraySize;

    private final SocketPool socketPool;
    private final boolean socketPooling;

    private final Pattern responsePattern;


    public ClamAvProgram() {
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_MAX_ARRAY_SIZE, false);
    }

    public ClamAvProgram(
            String socketHost,
            int socketPort,
            long maxArraySize,
            boolean socketPooling
    ) {
        this.socketHost = socketHost;
        this.socketPort = socketPort;
        this.maxArraySize = maxArraySize;

        final int socketCount = 5;
        socketPool = new SocketPool(socketCount, socketHost, socketPort, null);
        this.socketPooling = socketPooling;

        responsePattern = Pattern.compile(".+?: (.+)");
    }

    @Override
    public boolean scanBytes(byte[] bytes) throws ScanException {
        String response;
        try {
            response = scanBytesWithInfo(bytes);
        } catch (ScanException e) {
            log.warn(ERROR_MSG, e);
            throw new ScanException(ERROR_MSG, e);
        }

        if (response.equals(CLEAN_STREAM_RESPONSE)) {
            return false;
        } else {
            log.debug("Virus found: " + response);
            return true;
        }
    }

    /**
     * New checking prototype.
     *
     * @param bytes bytes for scan
     * @return the virus info
     * @throws ScanException if scan fails
     * @see ClamAvProgram#scanBytesWithInfo(byte[])
     */
    private String scanBytesPooling(byte[] bytes) throws ScanException {
        requireNonNull(bytes);

        final int maxAttempts = socketPool.getSize() + 1;
        for (int i = 0; i < maxAttempts; i++) {

            SocketPool.SocketWrapper socket = socketPool.getSocket();
            OutputStream outStream = socket.getOutputStream();
            BufferedReader in = socket.getBufferedReader();

            try {
                sendBytes(bytes, outStream);
                String response = in.readLine();

                return parseResponse(response);

            } catch (IOException e) {
                log.info(ERROR_MSG, e);
                socket.fix();
            } finally {
                socketPool.returnSocket(socket);
            }
        }

        throw new ScanException(ERROR_MSG);
    }

    public String parseResponse(String response) {
        Matcher matcher = responsePattern.matcher(response);
        if (matcher.matches()) {

            return matcher.group(1);
        }

        return "";
    }

    /**
     * <p>
     * <b>Clamd documentation:</b>
     * </p>
     * INSTREAM
     * <br>
     * It is mandatory to prefix this command with n or z.
     * <p>
     * Scan a stream of data. The stream is sent to  clamd  in  chunks,
     * after  INSTREAM,  on  the  same  socket on which the command was
     * sent.  This avoids the overhead of establishing new TCP
     * connections  and  problems  with  NAT.  The  format  of  the chunk is:
     * '{@literal <length><data>}' where {@literal <length>} is the
     * size  of  the  following
     * data  in bytes expressed as a 4 byte unsigned integer in network
     * byte order and {@literal <data>} is the actual chunk. Streaming
     * is  terminated  by  sending  a  zero-length  chunk.
     * Note:  do not exceed
     * StreamMaxLength as defined in clamd.conf, otherwise  clamd  will
     * reply  with  INSTREAM  size limit exceeded and close the
     * connection.
     * </p>
     */
    @Override
    public String scanBytesWithInfo(byte[] bytes) throws ScanException {
        if (socketPooling) {
            return scanBytesPooling(bytes);
        } else {
            return scanBytesNormal(bytes);
        }
    }

    private String scanBytesNormal(byte[] bytes) throws ScanException {
        requireNonNull(bytes);
        checkArraySize(bytes);

        String arrayDigest = null;
        if (caching) {
            arrayDigest = cachingService.arrayDigest(bytes);
            String cachedValue = cachingService.get(arrayDigest);
            if (cachedValue != null) {
                log.debug("Taking from the cache: " + arrayDigest);

                return cachedValue;
            }
        }

        log.debug("Scanning bytes...");
        try (
                Socket socket = createSocket();
                OutputStream outStream = socket.getOutputStream();
                InputStreamReader inReader =
                        new InputStreamReader(socket.getInputStream(), DEFAULT_CHARSET);
                BufferedReader in = new BufferedReader(inReader)
        ) {
            sendBytes(bytes, outStream);

            // read check result
            String response = in.readLine();

            log.debug("scanning done.");
            if (response != null) {
                addToCache(arrayDigest, response);

                return response;
            } else {
                log.warn("Response reading problem!");
                throw new ScanException(ERROR_MSG);
            }
        } catch (IOException e) {
            log.warn(ERROR_MSG, e);
            throw new ScanException(ERROR_MSG, e);
        }
    }

    private void sendBytes(byte[] bytes, OutputStream outStream) throws IOException {
        // write bytes
        outStream.write("nINSTREAM\n".getBytes("UTF-8"));
        outStream.write(intBytes(bytes.length, CHUNK_LENGTH_BYTE_SIZE));
        outStream.write(bytes);

        // terminate byte stream with a zero length chunk
        outStream.write(intBytes(0, CHUNK_LENGTH_BYTE_SIZE));
        outStream.flush();
    }

    private void addToCache(String arrayDigest, String response) {
        if (caching && arrayDigest != null) {
            log.debug("Adding to the cache: " + arrayDigest);
            cachingService.put(arrayDigest, response);
        }
    }

    private void checkArraySize(byte[] bytes) throws ScanException {
        if (bytes.length > getMaxArraySize()) {
            throw new ScanException(
                    "Array is too big: " + bytes.length + ", max is " + getMaxArraySize());
        }
    }

    private byte[] intBytes(int number, int size) {
        return ByteBuffer
                .allocate(size)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(number)
                .array();
    }

    @Override
    public String getNoVirusResponse() {
        return CLEAN_STREAM_RESPONSE;
    }

    @Override
    public boolean isRunning() {
        return testConnection();
    }

    @Override
    public void setCaching(boolean caching) {
        if (cachingService == null) {
            log.warn("Caching service is not set.");
            this.caching = false;

            return;
        }

        this.caching = caching;
    }

    @Override
    public boolean isCaching() {
        return caching;
    }

    private String command(String command) {
        String result = null;
        try (
                Socket socket = createSocket();
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), DEFAULT_CHARSET));
                InputStreamReader inReader =
                        new InputStreamReader(socket.getInputStream(), DEFAULT_CHARSET);
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
    @SuppressWarnings("try")
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

    @Override
    public long getMaxArraySize() {
        return maxArraySize;
    }

    @Autowired(required = false)
    public void setCachingService(CachingService cachingService) {
        this.cachingService = cachingService;
    }
}
