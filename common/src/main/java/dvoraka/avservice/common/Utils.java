package dvoraka.avservice.common;

import dvoraka.avservice.common.data.AVMessage;
import dvoraka.avservice.common.data.AVMessageType;
import dvoraka.avservice.common.data.DefaultAVMessage;

import java.nio.charset.StandardCharsets;

/**
 * Utility class.
 */
public final class Utils {

    public static final String EICAR =
            "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*";
    public static final String SERVICE_ID = "UTILS-SERVICE";


    private Utils() {
    }

    public static AVMessage genNormalMessage() {
        final int dataSize = 20;
        return new DefaultAVMessage.Builder(null)
                .serviceId(SERVICE_ID)
                .virusInfo("")
                .correlationId("1-2-3")
                .data(new byte[dataSize])
                .type(AVMessageType.REQUEST)
                .build();
    }

    public static AVMessage genInfectedMessage() {
        return new DefaultAVMessage.Builder(null)
                .serviceId(SERVICE_ID)
                .virusInfo("UNKNOWN")
                .correlationId("1-2-3")
                .data(EICAR.getBytes(StandardCharsets.UTF_8))
                .type(AVMessageType.REQUEST)
                .build();
    }
}
