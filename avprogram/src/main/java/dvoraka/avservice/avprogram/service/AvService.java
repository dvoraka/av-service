package dvoraka.avservice.avprogram.service;

import dvoraka.avservice.common.exception.ScanException;
import dvoraka.avservice.common.util.Utils;

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
     * @throws ScanException if scan fails
     */
    boolean scanBytes(byte[] bytes) throws ScanException;

    /**
     * Scans bytes and returns result as a string.
     * <p>
     * Value of {@link Utils#OK_VIRUS_INFO} is used
     * for the OK response string.
     *
     * @param bytes the bytes to scan
     * @return the result string
     * @throws ScanException if scan fails
     */
    String scanBytesWithInfo(byte[] bytes) throws ScanException;

    /**
     * Scans file and returns result as a boolean value.
     *
     * @param file the file for the scan
     * @return the result of scan
     * @throws ScanException if scan fails
     */
    boolean scanFile(File file) throws ScanException;

    /**
     * Returns the maximum size of a file.
     *
     * @return the max size
     */
    long getMaxFileSize();
}
