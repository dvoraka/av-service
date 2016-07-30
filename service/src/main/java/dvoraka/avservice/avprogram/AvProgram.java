package dvoraka.avservice.avprogram;

import dvoraka.avservice.common.exception.ScanErrorException;

/**
 * Anti-virus program interface.
 */
public interface AvProgram {

    /**
     * Scans the byte stream and returns the infected flag.
     *
     * @param bytes byte stream for scanning
     * @return the infected flag
     * @throws ScanErrorException when scanning failed
     */
    boolean scanStream(byte[] bytes) throws ScanErrorException;

    /**
     * Scans the byte stream and returns an info about found viruses.
     *
     * @param bytes byte stream for scanning
     * @return the virus description
     * @throws ScanErrorException when scanning failed
     */
    String scanStreamWithInfo(byte[] bytes) throws ScanErrorException;

    /**
     * Returns status of the program.
     *
     * @return the running status
     */
    boolean isRunning();

    /**
     * Sets the caching for the program.
     *
     * @param caching enable/disable caching
     */
    void setCaching(boolean caching);

    /**
     * Returns maximum stream size.
     *
     * @return the maximum stream size
     */
    long getMaxSize();
}
