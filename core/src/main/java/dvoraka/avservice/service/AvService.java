package dvoraka.avservice.service;

import dvoraka.avservice.common.exception.ScanErrorException;

import java.io.File;

/**
 * AV service interface.
 */
public interface AvService {

    /**
     * Scans bytes and returns result as a boolean value.
     *
     * @param bytes the bytes
     * @return the scan result
     * @throws ScanErrorException if scan fails
     */
    boolean scanBytes(byte[] bytes) throws ScanErrorException;

    /**
     * Scans bytes and returns result as a string.
     * <p>
     * Value of {@link dvoraka.avservice.common.Utils#OK_VIRUS_INFO} is used
     * for the OK response string.
     *
     * @param bytes the bytes to scan
     * @return the result string
     * @throws ScanErrorException if scan fails
     */
    String scanBytesWithInfo(byte[] bytes) throws ScanErrorException;

    /**
     * Scans file and returns result as a boolean value.
     *
     * @param file the file for the scan
     * @return the result of scan
     * @throws ScanErrorException if scan fails
     */
    boolean scanFile(File file) throws ScanErrorException;

    /**
     * Returns the maximum size of a file.
     *
     * @return the max size
     */
    long getMaxFileSize();
}
