package dvoraka.avservice;

/**
 * Anti-virus program interface.
 */
public interface AVProgram {

    boolean scanStream(byte[] bytes);
}
