package dvoraka.avservice.common.amqp;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.exception.MapperException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Spring AMQP AvMessage converter.
 */
@Component
public class AvMessageConverter implements MessageConverter {

    private final AvMessageMapper mapper;


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
                throw new MessageConversionException("Conversion failed.", e);
            }
        } else {
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
            throw new MessageConversionException("Conversion failed.", e);
        }

        return avMessage;
    }
}
