package dvoraka.avservice.avprogram;

/**
 * Anti-virus program interface.
 */
public interface AVProgram {

    boolean scanStream(byte[] bytes);

    boolean isRunning();
}
