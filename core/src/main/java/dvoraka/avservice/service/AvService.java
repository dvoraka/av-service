package dvoraka.avservice.service;

import dvoraka.avservice.common.exception.ScanErrorException;

import java.io.File;

/**
 * AV service interface.
 */
public interface AvService {

    boolean scanBytes(byte[] bytes) throws ScanErrorException;

    String scanBytesWithInfo(byte[] bytes) throws ScanErrorException;

    boolean scanFile(File file) throws ScanErrorException;

    long getMaxFileSize();
}
