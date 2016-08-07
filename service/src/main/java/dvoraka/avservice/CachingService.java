package dvoraka.avservice;

/**
 * Caching service interface.
 */
public interface CachingService {

    /**
     * Generates a digest for the given array of bytes.
     *
     * @param bytes the array of bytes
     * @return the digest
     */
    String arrayDigest(byte[] bytes);

    /**
     * Returns a cached info for the digest.
     *
     * @param digest the cached digest
     * @return the info or null if the info is not cached yet
     */
    String get(String digest);

    /**
     * Puts a digest into the cache with the given info.
     *
     * @param digest the digest
     * @param info   the info
     */
    void put(String digest, String info);

    /**
     * Returns the maximum size of cached files.
     *
     * @return the maximum size
     */
    long getMaxCachedFileSize();

    /**
     * Sets the maximum size of cached files.
     *
     * @param size the maximum size
     */
    void setMaxCachedFileSize(long size);

    /**
     * Returns the maximum items count in the cache.
     *
     * @return the size
     */
    long getMaxCacheSize();

    /**
     * Sets the maximum items count in the cache.
     *
     * @param size the max size
     */
    void setMaxCacheSize(long size);
}
