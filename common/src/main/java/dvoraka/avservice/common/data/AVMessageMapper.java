package dvoraka.avservice.common.data;

import dvoraka.avservice.common.exception.MapperException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * AVMessage mapper
 */
public final class AVMessageMapper {

    private static final Logger log = LogManager.getLogger(AVMessageMapper.class.getName());

    public static final String VIRUS_INFO_KEY = "virusInfo";
    public static final String DEFAULT_VIRUS_INFO = "noinfo";
    public static final String SERVICE_ID_KEY = "serviceId";
    public static final String DEFAULT_SERVICE_ID = "noservice";


    private AVMessageMapper() {
    }

    public static AVMessage transform(Message msg) throws MapperException {
        log.debug("Transform: " + msg);

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
        AVMessageType messageType;
        try {
            messageType = AVMessageType.valueOf(messageTypeStr);
        } catch (IllegalArgumentException e) {
            throw new MapperException("Unknown message type");
        }

        return new DefaultAVMessage.Builder(msgProps.getMessageId())
                .data(msg.getBody())
                .type(messageType)
                .virusInfo(virusInfo)
                .serviceId(serviceId)
                .build();
    }

    public static Message transform(AVMessage msg) throws MapperException {
        log.debug("AVTransform: " + msg);

        MessageProperties props = new MessageProperties();
        props.setMessageId(msg.getId());
        props.setCorrelationId(msg.getCorrelationId().getBytes(StandardCharsets.UTF_8));
        props.setType(msg.getType().toString());

        props.setAppId("antivirus");

        // for old clients
        int oldReply = msg.getVirusInfo().equals("") ? 1 : 0;
        props.setHeader("isClean", oldReply);

        // service ID
        props.setHeader(SERVICE_ID_KEY, msg.getServiceId());

        // virus info
        props.setHeader(VIRUS_INFO_KEY, msg.getVirusInfo());

        return new Message(msg.getData(), props);
    }
}
