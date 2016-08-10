package dvoraka.avservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Default caching service implementation.
 */
public class DefaultCachingService implements CachingService {

    private static final Logger log = LogManager.getLogger(DefaultCachingService.class.getName());

    public static final int DEFAULT_MAX_CACHED_FILE_SIZE = 5_000;
    public static final int DEFAULT_MAX_CACHE_SIZE = 10_000;

    private volatile long maxCachedFileSize;
    private volatile long maxCacheSize;

    private ConcurrentMap<String, String> scanCache;
    private Base64.Encoder b64encoder;
    private MessageDigest digest;


    public DefaultCachingService() {
        maxCachedFileSize = DEFAULT_MAX_CACHED_FILE_SIZE;
        maxCacheSize = DEFAULT_MAX_CACHE_SIZE;

        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.warn("Algorithm not found!", e);
        }

        if (digest == null) {
            throw new IllegalStateException("No algorithm found!");
        }

        scanCache = new ConcurrentHashMap<>();
        b64encoder = Base64.getEncoder();
    }

    @Override
    public String arrayDigest(byte[] bytes) {
        if (bytes.length > maxCachedFileSize) {
            return null;
        }
        synchronized (this) {
            return b64encoder.encodeToString(digest.digest(bytes));
        }
    }

    @Override
    public String get(String digest) {
        return scanCache.get(digest);
    }

    @Override
    public void put(String digest, String info) {
        if (digest == null || info == null) {
            return;
        }
        synchronized (this) {
            if (scanCache.size() < maxCacheSize) {
                scanCache.put(digest, info);
            }
        }
    }

    @Override
    public long getMaxCachedFileSize() {
        return maxCachedFileSize;
    }

    @Override
    public void setMaxCachedFileSize(long size) {
        this.maxCachedFileSize = size;
    }

    @Override
    public long getMaxCacheSize() {
        return maxCacheSize;
    }

    @Override
    public void setMaxCacheSize(long size) {
        this.maxCacheSize = size;
    }

    @Override
    public long cacheSize() {
        return scanCache.size();
    }
}
