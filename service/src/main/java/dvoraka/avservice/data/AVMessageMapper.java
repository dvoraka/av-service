package dvoraka.avservice.data;

import dvoraka.avservice.exception.MapperException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * AVMessage mapper
 */
public final class AVMessageMapper {

    public static final String VIRUS_INFO_KEY = "virusInfo";
    public static final String DEFAULT_VIRUS_INFO = "noinfo";
    public static final String SERVICE_ID_KEY = "serviceId";
    public static final String DEFAULT_SERVICE_ID = "noservice";


    private AVMessageMapper() {
    }

    public static AVMessage transform(Message msg) throws MapperException {
        MessageProperties msgProps = msg.getMessageProperties();
        Map<String, Object> headers = msgProps.getHeaders();

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

        // TODO: check message type
        return new DefaultAVMessage.Builder(msgProps.getMessageId())
                .data(msg.getBody())
                .type(AVMessageType.valueOf(msgProps.getType().toUpperCase()))
                .virusInfo(virusInfo)
                .serviceId(serviceId)
                .build();
    }

    public static Message transform(AVMessage msg) throws MapperException {
        MessageProperties props = new MessageProperties();
        props.setMessageId(msg.getId());
        props.setCorrelationId(msg.getCorrelationId().getBytes(StandardCharsets.UTF_8));
        props.setAppId("antivirus");
        props.setType(msg.getType().toString());

        int oldReply = msg.getVirusInfo().equals("") ? 1 : 0;
        props.setHeader("isClean", oldReply);
        props.setHeader("virusInfo", msg.getVirusInfo());

        return new Message(msg.getData(), props);
    }
}
