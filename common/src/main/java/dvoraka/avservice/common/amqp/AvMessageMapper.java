package dvoraka.avservice.common.amqp;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageType;
import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.common.exception.MapperException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * AMQP AV message mapper.
 */
public final class AvMessageMapper {

    private static final Logger log = LogManager.getLogger(AvMessageMapper.class.getName());

    public static final String VIRUS_INFO_KEY = "virusInfo";
    public static final String DEFAULT_VIRUS_INFO = "noinfo";
    public static final String SERVICE_ID_KEY = "serviceId";
    public static final String DEFAULT_SERVICE_ID = "noservice";


    private AvMessageMapper() {
    }

    /**
     * Transforms AMQP message to AV message.
     *
     * @param msg the AMQP message
     * @return the AV message
     * @throws MapperException if mapping failed
     */
    public static AvMessage transform(Message msg) throws MapperException {
        log.debug("Transform: " + msg);
        requireNonNull(msg, "Message may not be null!");

        MessageProperties props = msg.getMessageProperties();
        Map<String, Object> headers = props.getHeaders();

        checkMandatoryFields(props);

        // virus info
        String virusInfo = getVirusInfo(headers);
        // service ID
        String serviceId = getServiceId(headers);
        // message type
        AvMessageType messageType = getMessageType(props);
        // correlation ID
        String corrId = getCorrelationId(props);

        return new DefaultAvMessage.Builder(props.getMessageId())
                .correlationId(corrId)
                .data(msg.getBody())
                .type(messageType)
                .virusInfo(virusInfo)
                .serviceId(serviceId)
                .build();
    }

    private static void checkMandatoryFields(MessageProperties msgProps) throws MapperException {
        if (msgProps.getMessageId() == null) {
            throw new MapperException("Message ID can't be null");
        } else if (msgProps.getType() == null) {
            throw new MapperException("Message type can't be null");
        }
    }

    private static String getVirusInfo(Map<String, Object> headers) {
        Object virusInfoObj = headers.get(VIRUS_INFO_KEY);
        String virusInfo;
        if (virusInfoObj != null) {
            virusInfo = virusInfoObj.toString();
        } else {
            virusInfo = DEFAULT_VIRUS_INFO;
        }

        return virusInfo;
    }

    private static String getServiceId(Map<String, Object> headers) {
        Object serviceIdObj = headers.get(SERVICE_ID_KEY);
        String serviceId;
        if (serviceIdObj != null) {
            serviceId = serviceIdObj.toString();
        } else {
            serviceId = DEFAULT_SERVICE_ID;
        }

        return serviceId;
    }

    private static AvMessageType getMessageType(MessageProperties msgProps) throws MapperException {
        String messageTypeStr = msgProps.getType().toUpperCase();
        AvMessageType messageType;
        try {
            messageType = AvMessageType.valueOf(messageTypeStr);
        } catch (IllegalArgumentException e) {
            log.warn("Message type error!", e);
            throw new MapperException("Unknown message type");
        }

        return messageType;
    }

    private static String getCorrelationId(MessageProperties props) {
        String corrId = "";
        if (props.getCorrelationId() != null) {
            corrId = new String(props.getCorrelationId(), StandardCharsets.UTF_8);
        }

        return corrId;
    }

    /**
     * Transforms AV message to AMQP message.
     *
     * @param msg the AV message
     * @return the AMQP message
     * @throws MapperException if mapping failed
     */
    public static Message transform(AvMessage msg) throws MapperException {
        log.debug("AVTransform: " + msg);
        requireNonNull(msg, "Message may not be null!");

        // mandatory fields
        if (msg.getId() == null) {
            throw new MapperException("Message ID may not be null");
        } else if (msg.getType() == null) {
            throw new MapperException("Message type may not be null");
        }

        MessageProperties props = new MessageProperties();
        props.setMessageId(msg.getId());
        props.setType(msg.getType().toString());
        // correlation ID
        if (msg.getCorrelationId() != null) {
            props.setCorrelationId(msg.getCorrelationId().getBytes(StandardCharsets.UTF_8));
        }

        props.setAppId("antivirus");

        // service ID
        props.setHeader(SERVICE_ID_KEY, msg.getServiceId());
        // virus info
        props.setHeader(VIRUS_INFO_KEY, msg.getVirusInfo());

        return new Message(msg.getData(), props);
    }
}
