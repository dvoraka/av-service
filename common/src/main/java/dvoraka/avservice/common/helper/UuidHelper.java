package dvoraka.avservice.common.helper;

import java.util.UUID;

/**
 * UUID help methods.
 */
public interface UuidHelper {

    default String genUuidStr() {
        return UUID.randomUUID().toString();
    }
}
