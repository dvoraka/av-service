package dvoraka.avservice.common;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageType;
import dvoraka.avservice.common.data.DefaultAvMessage;

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

    public static AvMessage genNormalMessage() {
        final int dataSize = 20;
        return new DefaultAvMessage.Builder(null)
                .serviceId(SERVICE_ID)
                .virusInfo("")
                .correlationId("1-2-3")
                .data(new byte[dataSize])
                .type(AvMessageType.REQUEST)
                .build();
    }

    public static AvMessage genInfectedMessage() {
        return new DefaultAvMessage.Builder(null)
                .serviceId(SERVICE_ID)
                .virusInfo("UNKNOWN")
                .correlationId("1-2-3")
                .data(EICAR.getBytes(StandardCharsets.UTF_8))
                .type(AvMessageType.REQUEST)
                .build();
    }
}
