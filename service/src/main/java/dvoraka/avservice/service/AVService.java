package dvoraka.avservice.service;

import dvoraka.avservice.exception.ScanErrorException;

import java.io.File;

/**
 * AV service interface.
 */
public interface AVService {

    boolean scanStream(byte[] bytes);

    boolean scanFile(File file) throws ScanErrorException;
}
