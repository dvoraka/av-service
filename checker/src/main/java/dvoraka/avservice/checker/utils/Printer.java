package dvoraka.avservice.checker.utils;

import com.rabbitmq.client.AMQP;

import java.util.Map;

/**
 * Utility class for AMQP info printing.
 */
public final class Printer {


    private Printer() {
    }

    /**
     * Prints AMQP properties on stdout.
     *
     * @param props AMQP properties
     */
    public static void printProperties(AMQP.BasicProperties props) {
        StringBuilder message = new StringBuilder()
                .append("\tapp-id: ").append(props.getAppId()).append("\n")
                .append("\tcontent-encoding: ").append(props.getContentEncoding()).append("\n")
                .append("\tcontent-type: ").append(props.getContentType()).append("\n")
                .append("\tcorrelation-id: ").append(props.getCorrelationId()).append("\n")
                .append("\tmessage-id: ").append(props.getMessageId()).append("\n")
                .append("\ttimestamp: ").append(props.getTimestamp()).append("\n")
                .append("\ttype: ").append(props.getType());
        System.out.println(message.toString());
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
