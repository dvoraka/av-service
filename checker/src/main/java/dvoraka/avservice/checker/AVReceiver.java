package dvoraka.avservice.checker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Default AV receiver implementation.
 *
 * @author dvoraka
 */
public class AVReceiver implements Receiver {

    private static Logger logger = LogManager.getLogger();

    private String host;
    private String virtualHost;
    private boolean verboseOutput;


    public AVReceiver(String host) {

        this(host, true);
    }

    public AVReceiver(String host, boolean verboseOutput) {
        this(host, "antivirus", verboseOutput);
    }

    public AVReceiver(String host, String virtualHost, boolean verboseOutput) {
        this.host = host;
        this.virtualHost = virtualHost;
        this.verboseOutput = verboseOutput;
    }

    private ConnectionFactory prepareConnectionFactory(String host, String virtualHost) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setVirtualHost(virtualHost);

        return factory;
    }

    private Channel prepareChannel(Connection conn) throws IOException {
        Channel channel = conn.createChannel();
        channel.queueDeclare("test3", true, false, false, null);
        channel.queueBind("test3", "check-result", "");

        return channel;
    }

    private void printReceiveInfo(QueueingConsumer.Delivery delivery) {
        String message = new String(delivery.getBody());

        System.out.println("-------------");
        System.out.println(" [x] Received '" + message + "'");
        System.out.println("-------------");
        System.out.println("receive properties:\n" + delivery
                .getProperties());
        Printer.printProperties(delivery.getProperties());
        System.out.println("receive headers:\n" + delivery
                .getProperties().getHeaders());
    }

    private QueueingConsumer.Delivery nextDelivery(QueueingConsumer consumer)
            throws InterruptedException, LastMessageException {

        QueueingConsumer.Delivery delivery = consumer.nextDelivery(100L);
        if (delivery == null) {
            throw new LastMessageException();
        }

        return delivery;
    }

    private boolean isCleanHeaderValue(Map<String, Object> headers) {
        Object header = headers.get("isClean");
        if (header == null) {
            throw new IllegalArgumentException("isClean header missing");
        }

        int value = (Integer) header;

        return value == 0;
    }

    @Override
    public boolean receive(String corrId) throws
            IOException,
            InterruptedException,
            ProtocolException,
            LastMessageException {

        ConnectionFactory factory = prepareConnectionFactory(getHost(), getVirtualHost());
        Connection connection = null;
        Channel channel = null;
        boolean virus = true;

        try {
            connection = factory.newConnection();
            channel = prepareChannel(connection);

            if (isVerboseOutput()) {
                System.out.println(" [*] Waiting for messages...");
            }

            QueueingConsumer consumer = new QueueingConsumer(channel);
            // no ack before check
            channel.basicConsume("test3", false, consumer);

            QueueingConsumer.Delivery delivery = null;
            long dTag;
            while (true) {
                delivery = nextDelivery(consumer);
                dTag = delivery.getEnvelope().getDeliveryTag();
                if (isVerboseOutput()) {
                    printReceiveInfo(delivery);
                }

                Map<String, Object> headers = delivery.getProperties().getHeaders();
                virus = isCleanHeaderValue(headers);

                if (isVerboseOutput()) {
                    Printer.printHeaders(headers);
                }

                if (corrId.equals(delivery.getProperties().getCorrelationId())) {
                    if (isVerboseOutput()) {
                        System.out.println("Correlation ID match.");
                    }

                    channel.basicAck(dTag, false);

                    if (delivery.getProperties().getType().equals("response-error")) {
                        String errorMsg = headers.get("errorMsg").toString();
                        checkError(new ErrorMessage(errorMsg));
                    }

                    break;

                } else {
                    if (isVerboseOutput()) {
                        System.out.println("Message skipped.");
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("Connection problem - receive", e);
            throw e;
        } finally {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return virus;
    }

    private void checkError(ErrorMessage errorMessage) throws ProtocolException {
        errorMessage.check();
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return verbose output flag
     */
    public boolean isVerboseOutput() {
        return verboseOutput;
    }

    public boolean getVerboseOutput() {
        return verboseOutput;
    }

    public void setVerboseOutput(boolean verboseOutput) {
        this.verboseOutput = verboseOutput;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }
}
