package dvoraka.avservice.service;

import dvoraka.avservice.common.exception.ScanErrorException;

import java.io.File;

/**
 * AV service interface.
 */
public interface AvService {

    boolean scanStream(byte[] bytes) throws ScanErrorException;

    String scanStreamWithInfo(byte[] bytes) throws ScanErrorException;

    boolean scanFile(File file) throws ScanErrorException;

    long getMaxFileSize();
}
