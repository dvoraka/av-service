package dvoraka.avservice.service;

import dvoraka.avservice.common.exception.ScanErrorException;

import java.io.File;

/**
 * AV service interface.
 */
public interface AvService {

    boolean scanBytes(byte[] bytes) throws ScanErrorException;

    /**
     * Scans bytes and returns result as a string.
     * Value of {@link dvoraka.avservice.common.Utils#OK_VIRUS_INFO} is used
     * for the OK response string.
     *
     * @param bytes the bytes to scan
     * @return the result string
     * @throws ScanErrorException if scan failed
     */
    String scanBytesWithInfo(byte[] bytes) throws ScanErrorException;

    boolean scanFile(File file) throws ScanErrorException;

    long getMaxFileSize();
}
