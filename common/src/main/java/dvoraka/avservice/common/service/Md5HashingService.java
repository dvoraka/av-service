package dvoraka.avservice.common.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Hashing service with MD5 implementation.
 */
@Service
public class Md5HashingService implements HashingService {

    private static final Logger log = LogManager.getLogger(Md5HashingService.class);

    private final Base64.Encoder b64encoder;
    private MessageDigest digest;


    public Md5HashingService() {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.warn("Algorithm not found!", e);
        }

        if (digest == null) {
            throw new IllegalStateException("No algorithm found!");
        }

        b64encoder = Base64.getEncoder();
    }

    @Override
    public String arrayHash(byte[] data) {
        return b64encoder.encodeToString(digest.digest(data));
    }

    @Override
    public String stringHash(String data) {
        return arrayHash(data.getBytes(StandardCharsets.UTF_8));
    }
}
