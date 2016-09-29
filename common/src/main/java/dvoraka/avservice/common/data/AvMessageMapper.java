package dvoraka.avservice.common.data;

import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.exception.MapperException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * AvMessage mapper
 */
public final class AvMessageMapper {

    private static final Logger log = LogManager.getLogger(AvMessageMapper.class.getName());

    public static final String VIRUS_INFO_KEY = "virusInfo";
    public static final String OK_VIRUS_INFO = Utils.OK_VIRUS_INFO;
    public static final String DEFAULT_VIRUS_INFO = "noinfo";
    public static final String SERVICE_ID_KEY = "serviceId";
    public static final String DEFAULT_SERVICE_ID = "noservice";


    private AvMessageMapper() {
    }

    public static AvMessage transform(Message msg) throws MapperException {
        log.debug("Transform: " + msg);
        if (msg == null) {
            throw new IllegalArgumentException("Message may not be null!");
        }

        MessageProperties msgProps = msg.getMessageProperties();
        Map<String, Object> headers = msgProps.getHeaders();

        // checks for mandatory fields
        if (msgProps.getMessageId() == null) {
            throw new MapperException("Message ID can't be null");
        } else if (msgProps.getType() == null) {
            throw new MapperException("Message type can't be null");
        }

        // virus info
        Object virusInfoObj = headers.get(VIRUS_INFO_KEY);
        String virusInfo;
        if (virusInfoObj != null) {
            virusInfo = virusInfoObj.toString();
        } else {
            virusInfo = DEFAULT_VIRUS_INFO;
        }

        // service ID
        Object serviceIdObj = headers.get(SERVICE_ID_KEY);
        String serviceId;
        if (serviceIdObj != null) {
            serviceId = serviceIdObj.toString();
        } else {
            serviceId = DEFAULT_SERVICE_ID;
        }

        // message type checking
        String messageTypeStr = msgProps.getType().toUpperCase();
        AvMessageType messageType;
        try {
            messageType = AvMessageType.valueOf(messageTypeStr);
        } catch (IllegalArgumentException e) {
            log.warn("Message type error!", e);
            throw new MapperException("Unknown message type");
        }

        // correlation ID
        String corrId = "";
        if (msgProps.getCorrelationId() != null) {
            corrId = new String(msgProps.getCorrelationId(), StandardCharsets.UTF_8);
        }

        return new DefaultAvMessage.Builder(msgProps.getMessageId())
                .correlationId(corrId)
                .data(msg.getBody())
                .type(messageType)
                .virusInfo(virusInfo)
                .serviceId(serviceId)
                .build();
    }

    public static Message transform(AvMessage msg) throws MapperException {
        log.debug("AVTransform: " + msg);
        if (msg == null) {
            throw new IllegalArgumentException("Message may not be null!");
        }

        // mandatory fields
        if (msg.getId() == null) {
            throw new MapperException("Message ID may not be null");
        } else if (msg.getType() == null) {
            throw new MapperException("Message type may not be null");
        }

        MessageProperties props = new MessageProperties();
        props.setMessageId(msg.getId());
        props.setType(msg.getType().toString());

        if (msg.getCorrelationId() != null) {
            props.setCorrelationId(msg.getCorrelationId().getBytes(StandardCharsets.UTF_8));
        }

        props.setAppId("antivirus");

        // TODO: Is it necessary? Yes, current client still uses it and new response is not prepared.
        // for old clients (deprecated)
        int oldReply;
        if (msg.getVirusInfo() != null) {
            oldReply = OK_VIRUS_INFO.equals(msg.getVirusInfo()) ? 1 : 0;
        } else {
            oldReply = 1;
        }
        props.setHeader("isClean", oldReply);

        // service ID
        props.setHeader(SERVICE_ID_KEY, msg.getServiceId());

        // virus info
        props.setHeader(VIRUS_INFO_KEY, msg.getVirusInfo());

        return new Message(msg.getData(), props);
    }
}
