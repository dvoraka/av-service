package dvoraka.avservice.common.service;

/**
 * Hashing service for creating hashes from data.
 */
public interface HashingService {

    /**
     * Generates a hash value for a given array of bytes.
     *
     * @param bytes the array of bytes
     * @return the hash value
     */
    String arrayHash(byte[] bytes);

    /**
     * Generates a hash value for a given string.
     *
     * @param data the string
     * @return the hash value
     */
    String stringHash(String data);
}
