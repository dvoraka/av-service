package dvoraka.avservice.common.amqp

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.exception.MapperException
import org.springframework.amqp.core.Message
import org.springframework.amqp.support.converter.MessageConversionException
import spock.lang.Specification
import spock.lang.Subject

/**
 * AV message converter spec.
 */
class AvMessageConverterSpec extends Specification {

    @Subject
    AvMessageConverter converter

    AvMessageMapper mapper


    def setup() {
        mapper = new AvMessageMapper()
        converter = new AvMessageConverter(mapper)
    }

    def "conversion to and from Message"() {
        given:
            AvMessage avMessage = Utils.genInfectedMessage()

        when:
            Message message = converter.toMessage(avMessage, null)
        and:
            AvMessage convertedBackAvMessage = (AvMessage) converter.fromMessage(message)

        then:
            avMessage == convertedBackAvMessage
    }

    def "conversion to from null"() {
        when:
            converter.toMessage(null, null)

        then:
            thrown(MessageConversionException)
    }

    def "conversion from null"() {
        when:
            converter.fromMessage(null)

        then:
            thrown(NullPointerException)
    }

    def "conversion to with MapperException"() {
        given:
            mapper = Stub()
            mapper.transform((AvMessage) _) >> {
                throw new MapperException("TEST")
            }
            converter = new AvMessageConverter(mapper)

            AvMessage avMessage = Utils.genInfectedMessage()

        when:
            converter.toMessage(avMessage, null)

        then:
            thrown(MessageConversionException)
    }

    def "conversion from with MapperException"() {
        given:
            mapper = Stub()
            mapper.transform((Message) _) >> {
                throw new MapperException("TEST")
            }
            converter = new AvMessageConverter(mapper)

            AvMessage avMessage = Utils.genInfectedMessage()
            Message message = new AvMessageMapper().transform(avMessage)

        when:
            converter.fromMessage(message)

        then:
            thrown(MessageConversionException)
    }
}
