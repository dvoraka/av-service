package dvoraka.avservice.common;

import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.AVMessageType;
import dvoraka.avservice.data.DefaultAVMessage;

/**
 * Utility class.
 */
public class Utils {

    public static final String EICAR =
            "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*";


    public static AVMessage genNormalMessage() {
        return new DefaultAVMessage.Builder(null)
                .serviceId("TEST-SERVICE1")
                .virusInfo("UNKNOWN")
                .correlationId("1-2-3")
                .data(new byte[20])
                .type(AVMessageType.REQUEST)
                .build();
    }

    public static AVMessage genInfectedMessage() {
        return new DefaultAVMessage.Builder(null)
                .serviceId("TEST-SERVICE1")
                .virusInfo("UNKNOWN")
                .correlationId("1-2-3")
                .data(EICAR.getBytes())
                .type(AVMessageType.REQUEST)
                .build();
    }
}
