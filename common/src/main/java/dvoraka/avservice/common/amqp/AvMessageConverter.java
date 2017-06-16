package dvoraka.avservice.common.amqp;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MapperException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Spring AMQP AV message converter.
 *
 * @see AvMessage
 */
@Component
public class AvMessageConverter implements MessageConverter {

    private final AvMessageMapper mapper;

    private static final Logger log = LogManager.getLogger(AvMessageConverter.class);

    private static final String CONVERSION_FAILED = "Conversion failed!";


    @Autowired
    public AvMessageConverter(AvMessageMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) {
        Message message;
        if (object instanceof AvMessage) {
            try {
                message = mapper.transform((AvMessage) object);
            } catch (MapperException e) {
                log.warn(CONVERSION_FAILED, e);
                throw new MessageConversionException(CONVERSION_FAILED, e);
            }
        } else {
            log.warn("Conversion failed! Message is not an AvMessage instance.");
            throw new MessageConversionException("Message must be AvMessage.");
        }

        return message;
    }

    @Override
    public Object fromMessage(Message message) {
        AvMessage avMessage;
        try {
            avMessage = mapper.transform(message);
        } catch (MapperException e) {
            log.warn(CONVERSION_FAILED, e);
            throw new MessageConversionException(CONVERSION_FAILED, e);
        }

        return avMessage;
    }
}
