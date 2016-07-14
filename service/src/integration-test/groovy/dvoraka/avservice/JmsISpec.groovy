package dvoraka.avservice

import dvoraka.avservice.configuration.JmsConfig
import dvoraka.avservice.configuration.ServiceConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * JMS testing.
 */
@Ignore
@ContextConfiguration(classes = [JmsConfig.class])
@ActiveProfiles("jms")
class JmsISpec extends Specification {

    @Autowired
    JmsClient client;


    def "send message"() {
        expect:
        client.sendTestMessage()
    }
}
