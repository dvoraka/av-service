package dvoraka.avservice.avprogram;

import dvoraka.avservice.common.exception.ScanErrorException;

/**
 * Anti-virus program interface.
 */
public interface AvProgram {

    /**
     * Scans the bytes and returns the infected flag.
     *
     * @param bytes bytes for scanning
     * @return the infected flag
     * @throws ScanErrorException when scanning failed
     */
    boolean scanBytes(byte[] bytes) throws ScanErrorException;

    /**
     * Scans the bytes and returns an info about found viruses.
     *
     * @param bytes bytes for scanning
     * @return the virus description
     * @throws ScanErrorException when scanning failed
     */
    String scanBytesWithInfo(byte[] bytes) throws ScanErrorException;

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
