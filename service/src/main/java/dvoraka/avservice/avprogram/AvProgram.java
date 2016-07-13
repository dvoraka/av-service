package dvoraka.avservice.avprogram;

import dvoraka.avservice.common.exception.ScanErrorException;

/**
 * Anti-virus program interface.
 */
public interface AvProgram {

    boolean scanStream(byte[] bytes) throws ScanErrorException;

    String scanStreamWithInfo(byte[] bytes) throws ScanErrorException;

    boolean isRunning();
}
