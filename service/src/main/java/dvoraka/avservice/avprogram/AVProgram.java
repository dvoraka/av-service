package dvoraka.avservice.avprogram;

/**
 * Anti-virus program interface.
 */
public interface AVProgram {

    boolean scanStream(byte[] bytes);

    String scanStreamWithInfo(byte[] bytes);

    boolean isRunning();
}
