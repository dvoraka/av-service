package dvoraka.avservice.data;

import dvoraka.avservice.exception.MapperException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;

/**
 * AVMessage mapper
 */
public final class AVMessageMapper {

    public static final String VIRUS_INFO_KEY = "virusInfo";
    public static final String DEFAULT_VIRUS_INFO = "noinfo";


    private AVMessageMapper() {
    }

    public static AVMessage transform(Message msg) throws MapperException {
        MessageProperties msgProps = msg.getMessageProperties();
        // TODO: map headers
//        Map<String, Object> headers = msgProps.getHeaders();

        return new DefaultAVMessage.Builder(msgProps.getMessageId())
                .data(msg.getBody())
                .type(AVMessageType.valueOf(msgProps.getType()))
//                .virusInfo()
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
