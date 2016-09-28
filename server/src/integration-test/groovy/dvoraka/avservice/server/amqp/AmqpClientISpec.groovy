package dvoraka.avservice.server.amqp

import dvoraka.avservice.common.Utils
import dvoraka.avservice.server.configuration.AmqpClientConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * AMQP testing.
 */
@ContextConfiguration(classes = [AmqpClientConfig.class])
@ActiveProfiles(["amqp-client"])
class AmqpClientISpec extends Specification {

    @Autowired
    Environment env
    @Autowired
    AmqpClient amqpClient


    def "send message to check exchange"() {
        when:
            amqpClient.sendMessage(
                    Utils.genNormalMessage(),
                    env.getProperty("avservice.amqp.checkExchange", "check"))

        then:
            notThrown(Exception)
    }
}
