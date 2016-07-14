package dvoraka.avservice.checker.receiver.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import dvoraka.avservice.checker.ErrorMessage;
import dvoraka.avservice.checker.exception.LastMessageException;
import dvoraka.avservice.checker.exception.ProtocolException;
import dvoraka.avservice.checker.receiver.AvReceiver;
import dvoraka.avservice.checker.utils.Printer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * AMQP receiver implementation.
 */
public class AmqpReceiver implements AvReceiver {

    private static final Logger log = LogManager.getLogger();

    private static final String DEFAULT_VHOST = "antivirus";
    private static final String DEFAULT_QUEUE = "av-result";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final long DEFAULT_RCV_TIMEOUT = 200L;

    private String host;
    private String virtualHost;
    private boolean verboseOutput;
    private long receiveTimeout;


    public AmqpReceiver(String host) {
        this(host, true);
    }

    public AmqpReceiver(String host, boolean verboseOutput) {
        this(host, DEFAULT_VHOST, verboseOutput);
    }

    public AmqpReceiver(String host, String virtualHost, boolean verboseOutput) {
        this.host = host;
        this.virtualHost = virtualHost;
        this.verboseOutput = verboseOutput;
        this.receiveTimeout = DEFAULT_RCV_TIMEOUT;
    }

    private ConnectionFactory prepareConnectionFactory(String host, String virtualHost) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setVirtualHost(virtualHost);

        return factory;
    }

    private Channel prepareChannel(Connection conn) throws IOException {
        Channel channel = conn.createChannel();
//        channel.queueDeclare("test3", true, false, false, null);
//        channel.queueBind("test3", "check-result", "");

        return channel;
    }

    private void printReceiveInfo(QueueingConsumer.Delivery delivery) {
        if (isVerboseOutput()) {
            String message = new String(delivery.getBody(), DEFAULT_CHARSET);

            System.out.println("-------------");
            System.out.println(" [x] Received '" + message + "'");
            System.out.println("-------------");
            System.out.println("receive properties:\n" + delivery
                    .getProperties());
            Printer.printProperties(delivery.getProperties());
            System.out.println("receive headers:\n" + delivery
                    .getProperties().getHeaders());
        }
    }

    private QueueingConsumer.Delivery nextDelivery(QueueingConsumer consumer)
            throws InterruptedException, LastMessageException {

        QueueingConsumer.Delivery delivery = consumer.nextDelivery(receiveTimeout);
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
            ProtocolException,
            LastMessageException {

        ConnectionFactory factory = prepareConnectionFactory(getHost(), getVirtualHost());
        Connection connection = null;
        Channel channel = null;
        boolean virus = true;
        try {
            connection = factory.newConnection();
            channel = prepareChannel(connection);

            printMessage(" [*] Waiting for messages...");
            QueueingConsumer consumer = new QueueingConsumer(channel);
            // no ack before check
            channel.basicConsume(DEFAULT_QUEUE, false, consumer);

            QueueingConsumer.Delivery delivery;
            long dTag;
            while (true) {
                delivery = nextDelivery(consumer);
                printReceiveInfo(delivery);
                dTag = delivery.getEnvelope().getDeliveryTag();

                Map<String, Object> headers = extractHeaders(delivery);
                virus = isCleanHeaderValue(headers);

                if (corrId.equals(delivery.getProperties().getCorrelationId())) {
                    printMessage("Correlation ID match.");
                    channel.basicAck(dTag, false);
                    findError(delivery, headers);

                    break;
                } else {
                    printMessage("Message skipped.");
                }
            }
        } catch (IOException e) {
            log.warn("Connection problem - receive", e);
            throw e;
        } catch (TimeoutException e) {
            log.warn(e);
        } catch (InterruptedException e) {
            log.warn("Receiving interrupted!");
            Thread.currentThread().interrupt();
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (TimeoutException | IOException e) {
                    log.warn(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    log.warn(e);
                }
            }
        }

        return virus;
    }

    private void findError(QueueingConsumer.Delivery delivery, Map<String, Object> headers) throws ProtocolException {
        if ("response-error".equals(delivery.getProperties().getType())) {
            String errorMsg = headers.get("errorMsg").toString();
            checkError(new ErrorMessage(errorMsg));
        }
    }

    private Map<String, Object> extractHeaders(QueueingConsumer.Delivery delivery) {
        Map<String, Object> headers = delivery.getProperties().getHeaders();
        if (isVerboseOutput()) {
            Printer.printHeaders(headers);
        }

        return headers;
    }

    private void printMessage(String msg) {
        if (isVerboseOutput()) {
            System.out.println(msg);
        }
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
    @Override
    public boolean isVerboseOutput() {
        return verboseOutput;
    }

    @Override
    public void setVerboseOutput(boolean verboseOutput) {
        this.verboseOutput = verboseOutput;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }
}
