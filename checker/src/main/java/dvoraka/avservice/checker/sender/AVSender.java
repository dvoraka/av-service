package dvoraka.avservice.checker.sender;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dvoraka.avservice.checker.utils.Printer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Anti-virus file sender class
 */
public class AVSender implements Sender {

    private static Logger logger = LogManager.getLogger();

    private static final String DEFAULT_VHOST = "antivirus";
    private static final String DEFAULT_CHECK_EXCHANGE = "check";

    /**
     * AMQP message broker host
     */
    private final String host;
    /**
     * Sender protocol version
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


    public AVSender(String host) {
        this(host, true);
    }

    public AVSender(String host, boolean verboseOutput) {
        this(host, verboseOutput, "1");
    }

    public AVSender(String host, boolean verboseOutput, String protocolVersion) {
        this(host, verboseOutput, protocolVersion, DEFAULT_VHOST, DEFAULT_CHECK_EXCHANGE);
    }

    public AVSender(
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

            if (isVerboseOutput()) {
                System.out.println("-------------");
                System.out.println("Message sent.");
            }

        } catch (IOException e) {
            logger.warn("Connection problem - send", e);
            throw e;
//        } catch (InterruptedException e) {
//            logger.warn("Connection problem - send interrupted", e);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.close();
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
            logger.warn("Connection problem - purge queue", e);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.close();
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
     * Returns file bytes.
     *
     * @param virus include virus flag
     * @return file as bytes
     * @throws FileNotFoundException if the virus file is not found
     */
    public byte[] readTestFile(boolean virus)
            throws FileNotFoundException {

        byte[] bytes;
        if (virus) {
            // read EICAR
            InputStream in = getClass().getResourceAsStream("/eicar");
            if (in == null) {
                logger.warn("Virus file not found.");
                throw new FileNotFoundException();
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[10];
            try {
                for (int readNum; (readNum = in.read(buf)) != -1; ) {
                    bos.write(buf, 0, readNum);
                }
            } catch (IOException e) {
                logger.warn("Virus file problem.", e);
            }

            bytes = bos.toByteArray();
        } else {
            String testStr = "Test string!!!";
            bytes = testStr.getBytes();
        }

        return bytes;
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
    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    /**
     * @return the verbose output flag
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

    public String getRequestExchange() {
        return requestExchange;
    }

    public void setRequestExchange(String requestExchange) {
        this.requestExchange = requestExchange;
    }
}
