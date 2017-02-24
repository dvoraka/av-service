package dvoraka.avservice.common;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageInfo;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.common.data.DefaultAvMessageInfo;
import dvoraka.avservice.common.data.MessageType;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

/**
 * Utility class mainly for testing.
 */
public final class Utils {

    public static final String EICAR =
            "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*";
    public static final String SERVICE_TEST_ID = "TEST-SERVICE";
    public static final String OK_VIRUS_INFO = "stream: OK";
    public static final String TEST_CORR_ID = "1-2-3";


    private Utils() {
    }

    public static String genUuidString() {
        return UUID.randomUUID().toString();
    }

    public static AvMessage genMessage() {
        final int dataSize = 20;
        return new DefaultAvMessage.Builder(genUuidString())
                .correlationId(TEST_CORR_ID)
                .data(new byte[dataSize])
                .type(MessageType.REQUEST)
                .build();
    }

    public static AvMessage genInfectedMessage() {
        return new DefaultAvMessage.Builder(genUuidString())
                .correlationId(TEST_CORR_ID)
                .data(EICAR.getBytes(StandardCharsets.UTF_8))
                .type(MessageType.REQUEST)
                .build();
    }

    public static AvMessage genFileMessage() {
        return genFileMessage(genUuidString());
    }

    public static AvMessage genFileMessage(String username) {
        final int dataSize = 40;
        return new DefaultAvMessage.Builder(genUuidString())
                .correlationId(TEST_CORR_ID)
                .data(new byte[dataSize])
                .type(MessageType.FILE_SAVE)
                .filename("testFilename" + genUuidString()) //TODO
                .owner(username)
                .build();
    }

    public static AvMessage genInfectedFileMessage() {
        return genInfectedFileMessage(genUuidString());
    }

    public static AvMessage genInfectedFileMessage(String username) {
        return new DefaultAvMessage.Builder(genUuidString())
                .correlationId(TEST_CORR_ID)
                .data(EICAR.getBytes(StandardCharsets.UTF_8))
                .type(MessageType.FILE_SAVE)
                .filename("testFilename" + genUuidString()) //TODO
                .owner(username)
                .build();
    }

    public static AvMessageInfo genAvMessageInfo(AvMessageSource source) {
        return new DefaultAvMessageInfo.Builder(genUuidString())
                .source(source)
                .serviceId(SERVICE_TEST_ID)
                .created(Instant.now())
                .build();
    }
}
