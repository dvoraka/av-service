package dvoraka.avservice.checker.utils;

import com.rabbitmq.client.AMQP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Class for AMQP info printing.
 *
 * @author dvoraka
 */
public final class Printer {

    private static Logger logger = LogManager.getLogger();


    private Printer() {
    }

    /**
     * Prints AMQP properties on stdout.
     *
     * @param props AMQP properties
     */
    public static void printProperties(AMQP.BasicProperties props) {
        System.out.println("\tapp-id: " + props.getAppId());
        System.out.println("\tcontent-encoding: " + props.getContentEncoding());
        System.out.println("\tcontent-type: " + props.getContentType());
        System.out.println("\tcorrelation-id: " + props.getCorrelationId());
        System.out.println("\tmessage-id: " + props.getMessageId());
        System.out.println("\ttimestamp: " + props.getTimestamp());
        System.out.println("\ttype: " + props.getType());
    }

    /**
     * Prints AMQP headers on stdout.
     *
     * @param headers AMQP headers
     */
    public static void printHeaders(Map<String, Object> headers) {
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
        }
    }
}
