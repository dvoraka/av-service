package dvoraka.avservice.server.amqp

import dvoraka.avservice.common.Utils
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification

/**
 * AMQP testing.
 */
class AmqpClientISpec extends Specification {

    @Autowired
    AmqpClient amqpClient


    @Ignore
    def "send message"() {
        expect:
            amqpClient.sendMessage(Utils.genNormalMessage(), "")
    }
}
