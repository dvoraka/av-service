package dvoraka.avservice.server.amqp

import dvoraka.avservice.common.Utils
import dvoraka.avservice.server.configuration.AmqpConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * AMQP testing.
 */
@ContextConfiguration(classes = [AmqpConfig.class])
@ActiveProfiles(["amqp", "database"])
class AmqpClientISpec extends Specification {

    @Autowired
    Environment env
    @Autowired
    AmqpClient amqpClient


    @Ignore
    def "send message"() {
        when:
            amqpClient.sendMessage(
                    Utils.genNormalMessage(),
                    env.getProperty("avservice.amqp.checkExchange", "check"))

        then:
            notThrown(Exception)
    }
}
