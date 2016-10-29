package dvoraka.avservice.common.amqp

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import org.springframework.amqp.core.Message
import spock.lang.Specification

/**
 * AV message converter test.
 */
class AvMessageConverterSpec extends Specification {

    AvMessageConverter converter


    def setup() {
        converter = new AvMessageConverter()
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
}
