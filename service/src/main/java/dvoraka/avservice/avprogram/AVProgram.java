package dvoraka.avservice.avprogram;

import dvoraka.avservice.exception.ScanErrorException;

/**
 * Anti-virus program interface.
 */
public interface AVProgram {

    boolean scanStream(byte[] bytes) throws ScanErrorException;

    String scanStreamWithInfo(byte[] bytes) throws ScanErrorException;

    boolean isRunning();
}
