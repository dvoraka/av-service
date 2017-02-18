package dvoraka.avservice.avprogram;

import dvoraka.avservice.common.exception.ScanException;

/**
 * Anti-virus program interface.
 */
public interface AvProgram {

    /**
     * Scans the bytes and returns the infected flag.
     *
     * @param bytes bytes for scanning
     * @return the infected flag
     * @throws ScanException when scanning failed
     */
    boolean scanBytes(byte[] bytes) throws ScanException;

    /**
     * Scans the bytes and returns an info about found viruses.
     *
     * @param bytes bytes for scanning
     * @return the virus description
     * @throws ScanException when scanning failed
     */
    String scanBytesWithInfo(byte[] bytes) throws ScanException;

    /**
     * Returns the concrete string which means no virus in a message.
     * <p>
     * Every program can have different string for success so it is good to know what means OK.
     *
     * @return the string with OK meaning
     */
    String getNoVirusResponse();

    /**
     * Returns status of the program.
     *
     * @return the running status
     */
    boolean isRunning();

    /**
     * Returns the caching status.
     *
     * @return the caching status
     */
    boolean isCaching();

    /**
     * Sets the caching for the program.
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
