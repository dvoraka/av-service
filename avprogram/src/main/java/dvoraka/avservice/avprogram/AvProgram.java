package dvoraka.avservice.avprogram;

import dvoraka.avservice.common.exception.ScanException;

/**
 * Anti-virus program interface.
 */
public interface AvProgram {

    /**
     * Scans bytes and returns an infected flag.
     *
     * @param bytes bytes for scanning
     * @return the infected flag
     * @throws ScanException when scanning failed
     */
    boolean scanBytes(byte[] bytes) throws ScanException;

    /**
     * Scans bytes and returns an info about found viruses.
     *
     * @param bytes bytes for scanning
     * @return the virus description
     * @throws ScanException when scanning failed
     * @see #getNoVirusResponse()
     */
    String scanBytesWithInfo(byte[] bytes) throws ScanException;

    /**
     * Returns a concrete string which means no virus in a message.
     * <p>
     * Every program can have different string for success so it is good to know what means OK.
     *
     * @return the string with OK meaning
     */
    String getNoVirusResponse();

    /**
     * Returns a running state of the program.
     *
     * @return the running state
     */
    boolean isRunning();

    /**
     * Returns a caching status.
     *
     * @return the caching status
     */
    boolean isCaching();

    /**
     * Sets a caching for the program.
     *
     * @param caching enable/disable caching
     */
    void setCaching(boolean caching);

    /**
     * Returns maximum byte array size.
     *
     * @return the maximum stream size
     */
    long getMaxArraySize();
}
