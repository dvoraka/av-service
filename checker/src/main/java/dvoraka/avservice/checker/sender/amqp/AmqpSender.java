package dvoraka.avservice.checker.sender.amqp;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dvoraka.avservice.checker.sender.AvSender;
import dvoraka.avservice.checker.utils.Printer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * AMQP file sender class
 */
public class AmqpSender implements AvSender {

    private static final Logger log = LogManager.getLogger();

    private static final String DEFAULT_VHOST = "antivirus";
    private static final String DEFAULT_CHECK_EXCHANGE = "check";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * AMQP message broker host
     */
    private final String host;
    /**
     * AvSender protocol version
     */
    private String protocolVersion;
    /**
     * Verbose output flag
     */
    private boolean verboseOutput;
    /**
     * Request exchange name
     */
    private String requestExchange;

    private ConnectionFactory conFactory;


    public AmqpSender(String host) {
        this(host, true, "1");
    }

    public AmqpSender(String host, boolean verboseOutput, String protocolVersion) {
        this(host, verboseOutput, protocolVersion, DEFAULT_VHOST, DEFAULT_CHECK_EXCHANGE);
    }

    public AmqpSender(
            String host,
            boolean verboseOutput,
            String protocolVersion,
            String virtualHost,
            String requestExchange) {

        this.host = host;
        this.verboseOutput = verboseOutput;
        this.protocolVersion = protocolVersion;

        this.requestExchange = requestExchange;

        conFactory = new ConnectionFactory();
        conFactory.setHost(getHost());
        conFactory.setVirtualHost(virtualHost);
    }

    public String getHost() {
        return host;
    }

    /**
     * Sends testing file through AMQP.
     *
     * @param virus dirty file flag
     * @param appId application ID field
     * @throws java.io.IOException
     */
    @Override
    public String sendFile(boolean virus, String appId)
            throws java.io.IOException {

        String messageId = UUID.randomUUID().toString();
        AMQP.BasicProperties props = prepareProperties(appId, messageId);

        if (isVerboseOutput()) {
            printSendingProperties(props);
        }

        Connection connection = null;
        Channel channel = null;
        byte[] bytes = readTestFile(virus);

        // send message to "check" exchange
        try {
            connection = conFactory.newConnection();
            channel = connection.createChannel();
//            setChannelConfirming(channel);

            channel.basicPublish(getRequestExchange(), "", props, bytes);
//            channel.waitForConfirmsOrDie();

            printMessage("-------------");
            printMessage("Message sent.");

        } catch (IOException e) {
            log.warn("Connection problem - send", e);
            throw e;
//        } catch (InterruptedException e) {
//            log.warn("Connection problem - send interrupted", e);
        } catch (TimeoutException e) {
            log.warn(e);
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

        return messageId;
    }

//    private void createAndBindResponseQueue(Channel channel) throws IOException {
//        channel.queueDeclare(getResponseQueue(), true, false, false, null);
//        channel.queueBind(getResponseQueue(), getResponseExchange(), "");
//
//        setResponseQueueCreated(true);
//    }

    private void setChannelConfirming(Channel channel) throws IOException {
        channel.addConfirmListener(new ConfirmListener() {

            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
//                System.out.println("ACK");
//                System.out.println("M: " + multiple);
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
//                System.out.println("NACK");
//                System.out.println("M: " + multiple);
            }
        });

        channel.confirmSelect();
    }

    private void printSendingProperties(AMQP.BasicProperties props) {
        System.out.println("send properties:\n" + props);
        Printer.printProperties(props);
        System.out.println("send headers:\n" + props.getHeaders());
        Printer.printHeaders(props.getHeaders());
    }

    /**
     * Prepares AMQP properties.
     *
     * @param appId     the application ID
     * @param messageId the message ID
     * @return AMQP properties
     */
    private AMQP.BasicProperties prepareProperties(String appId, String messageId) {
        return new AMQP.BasicProperties.Builder()
                .appId(appId)
                .contentEncoding("binary")
                .contentType("application/octet-stream")
                .correlationId(null)
                .messageId(messageId)
                .type("request")
                .headers(prepareHeaders())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void purgeQueue(String queueName) throws IOException {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = conFactory.newConnection();
            channel = connection.createChannel();
            channel.queuePurge(queueName);
        } catch (IOException e) {
            log.warn("Connection problem - purge queue", e);
        } catch (TimeoutException e) {
            log.warn(e);
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
    }

    /**
     * Prepares AMQP headers.
     *
     * @return AMQP headers
     */
    private Map<String, Object> prepareHeaders() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("header1", "test");
        headers.put("protocol", getProtocolVersion());

        return headers;
    }

    /**
     * Returns the file as bytes.
     *
     * @param virus include virus flag
     * @return file as bytes
     */
    private byte[] readTestFile(boolean virus) {
        byte[] bytes;
        if (virus) {
            bytes = infectedTestFileBytes();
        } else {
            bytes = cleanTestFileBytes();
        }

        return bytes;
    }

    private byte[] infectedTestFileBytes() {
        byte[] bytes = null;
        // read EICAR
        try (InputStream in = getClass().getResourceAsStream("/eicar")) {
            if (in == null) {
                log.warn("Virus file not found.");
                throw new FileNotFoundException("Virus file not found.");
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final int bufferSize = 10;
            byte[] buffer = new byte[bufferSize];

            int readNum;
            while ((readNum = in.read(buffer)) != -1) {
                bos.write(buffer, 0, readNum);
            }

            bytes = bos.toByteArray();
        } catch (IOException e) {
            log.warn("Virus file problem!", e);
        }

        return bytes;
    }

    private byte[] cleanTestFileBytes() {
        String testStr = "Test string!!!";

        return testStr.getBytes(DEFAULT_CHARSET);
    }

    private void printMessage(String msg) {
        if (isVerboseOutput()) {
            System.out.println(msg);
        }
    }

    /**
     * @return the protocolVersion
     */
    public String getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * @param protocolVersion the protocolVersion to set
     */
    @Override
    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    /**
     * @return the verbose output flag
     */
    @Override
    public boolean isVerboseOutput() {
        return verboseOutput;
    }

    @Override
    public void setVerboseOutput(boolean verboseOutput) {
        this.verboseOutput = verboseOutput;
    }

    public String getRequestExchange() {
        return requestExchange;
    }

    public void setRequestExchange(String requestExchange) {
        this.requestExchange = requestExchange;
    }
}
